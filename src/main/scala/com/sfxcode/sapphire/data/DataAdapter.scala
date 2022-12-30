package com.sfxcode.sapphire.data

import com.sfxcode.sapphire.data.el.{ Expressions, ObjectExpressionHelper }
import com.sfxcode.sapphire.data.reflect.FieldMeta._
import com.sfxcode.sapphire.data.reflect.{ FieldMeta, FieldRegistry, ReflectionTools }

import java.lang.reflect.Field
import java.util
import scala.collection.{ immutable, mutable }
import scala.jdk.CollectionConverters._

class DataAdapter[T <: AnyRef](val wrappedData: T, typeHints: List[FieldMeta] = EmptyTypeHints)
  extends ValueHelper
  with java.util.Map[String, Any]  {

  val reflectedFields: immutable.Map[String, Field] = FieldRegistry.fieldMap(wrappedData.getClass)
  val changeManagementMap: mutable.HashMap[String, Any] = new mutable.HashMap[String, Any]()
  val childrenMap: mutable.HashMap[String, DataAdapter[AnyRef]] =
    new mutable.HashMap[String, DataAdapter[AnyRef]]()
  var trackChanges: Boolean = true
  var parentDataAdapter: Option[DataAdapter[AnyRef]] = None

  def data: AnyRef = wrappedData

  def apply(key: String): Any =
    value(key)

  protected def shouldHandleRelations(key: String): Boolean =
    key.contains(".") && !key.contains(ObjectExpressionHelper.ExpressionPrefix)

  def value(key: String): Any =
    wrappedData match {
      case map: scala.collection.Map[String, Any] => map(key)
      case javaMap: java.util.Map[String, Any] => javaMap.get(key)
      case _ =>
        if (shouldHandleRelations(key)) {
          val objectKey = key.substring(0, key.indexOf("."))
          val newKey = key.substring(key.indexOf(".") + 1)
          val relatedObject = value(objectKey)
          val childBean = createChildForKey(objectKey, relatedObject)
          childBean.value(newKey)
        } else {
          try getValueForExpression(key).get
          catch {
            case e: Exception =>
              logger.warn(e.getMessage, e)
              null
          }
        }
    }

  def oldValue(key: String): Any =
    if (trackChanges) {
      changeManagementMap.get(key).getOrElse(value(key))
    } else {
      value(key)
    }

  def getValueForExpression[T <: Any](expression: String): Option[T] =
    Expressions.evaluateExpressionOnObject[T](wrappedData, expression)

  def updateValues(map: Map[String, Any]): Unit =
    map.keySet.foreach { key =>
      updateValue(key, map.get(key).orNull)
    }

  def updateValue(key: String, valueToUpdate: Any): Unit = {
    var newValue = valueToUpdate
    val oldValue = value(key)
    if (valueToUpdate == None) {
      newValue = null
    }
    wrappedData match {
      case map: mutable.Map[String, Any] =>
        map.put(key, newValue)
        preserveChanges(key, oldValue, newValue)
      case javaMap: java.util.Map[String, Any] =>
        javaMap.put(key, newValue)
        preserveChanges(key, oldValue, newValue)
      case _ =>
        if (shouldHandleRelations(key)) {
          val objectKey = key.substring(0, key.indexOf("."))
          val newKey = key.substring(key.indexOf(".") + 1)
          val relatedObject = value(objectKey)
          val childBean = createChildForKey(objectKey, relatedObject)
          childBean.updateValue(newKey, valueToUpdate)
        } else {
          ReflectionTools.setFieldValue(wrappedData, key, newValue)
          childrenMap.remove(key)
          preserveChanges(key, oldValue, newValue)

        }
    }
  }

  protected def createChildForKey(key: String, value: Any): DataAdapter[AnyRef] = {

    if (!childrenMap.contains(key)) {
      val adapter = DataAdapter(value.asInstanceOf[AnyRef])
      adapter.parentDataAdapter = Some(this.asInstanceOf[DataAdapter[AnyRef]])
      adapter.trackChanges = trackChanges
      childrenMap.+=(key -> adapter)
    }
    childrenMap(key)
  }

  def preserveChanges(key: String, oldValue: Any, newValue: Any): Unit =
    if (trackChanges) {
      if (changeManagementMap.contains(key)) {
        if (newValue == changeManagementMap(key) || newValue.equals(changeManagementMap(key))) {
          changeManagementMap.remove(key)
        }
      } else {
        changeManagementMap.put(key, oldValue)
      }
    }

  def revert(): Unit =
    if (trackChanges) {
      trackChanges = false
      changeManagementMap.keySet.foreach { key =>
        val oldValue = changeManagementMap(key)
        updateValue(key, oldValue)
      }
      childrenMap.keySet.foreach(key => childrenMap(key).revert())
      trackChanges = true
      clearChanges()
    }
  def hasChanges(): Boolean = {
    if (changeManagementMap.nonEmpty) {
      return true
    }
    childrenMap.values.foreach(dataAdapter =>
      if (dataAdapter.hasChanges()) {
        return true
      })
    false
  }

  def clearChanges(): Unit =
    if (trackChanges) {
      changeManagementMap.clear()
      childrenMap.values.foreach(_.clearChanges())
    }

  override def toString: String =
    "{%s : %s@%s}".format(super.toString, wrappedData, wrappedData.hashCode())

  // java.util.Map Fascade
  override def size(): Int =
    wrappedData match {
      case map: scala.collection.Map[_, _] => map.size
      case javaMap: java.util.Map[String, Any] => javaMap.size()
      case _ => reflectedFields.size
    }

  override def isEmpty: Boolean =
    size() == 0

  override def containsKey(key: Any): Boolean =
    wrappedData match {
      case map: scala.collection.Map[String, _] => map.contains(key.toString)
      case javaMap: java.util.Map[String, Any] => javaMap.containsKey(key)
      case _ => reflectedFields.keySet.contains(key.toString)
    }

  override def containsValue(value: Any): Boolean =
    wrappedData match {
      case map: scala.collection.Map[_, _] => map.values.toList.contains(value)
      case javaMap: java.util.Map[String, Any] => javaMap.containsValue(value)
      case _ => FieldRegistry.memberMap(wrappedData).values.toList.contains(value)
    }

  override def get(key: Any): Any = value(key.toString)

  override def put(key: String, value: Any): Any = updateValue(key, value)

  override def remove(key: Any): Any =
    wrappedData match {
      case map: mutable.Map[String, Any] => map.remove(key.toString)
      case javaMap: java.util.Map[String, Any] => javaMap.remove(key)
      case _ =>
    }

  override def putAll(map: java.util.Map[_ <: String, _]): Unit =
    map.keySet().asScala.foreach { key =>
      updateValue(key, map.get(key))
    }

  override def clear(): Unit =
    wrappedData match {
      case map: mutable.Map[String, Any] => map.clear()
      case javaMap: java.util.Map[String, Any] => javaMap.clear()
      case _ =>
    }

  override def keySet(): java.util.Set[String] = wrappedData match {
    case map: scala.collection.Map[String, Any] => map.keySet.asJava
    case javaMap: java.util.Map[String, Any] => javaMap.keySet
    case _ => FieldRegistry.memberMap(wrappedData).keySet.asJava
  }

  override def values(): java.util.Collection[Any] =
    wrappedData match {
      case map: scala.collection.Map[String, Any] => map.values.asJavaCollection
      case javaMap: java.util.Map[String, Any] => javaMap.values()
      case _ => FieldRegistry.memberMap(wrappedData).values.asJavaCollection
    }

  override def entrySet(): java.util.Set[util.Map.Entry[String, Any]] =
    new java.util.HashSet[util.Map.Entry[String, Any]]()
}

object DataAdapter {

  def apply[T <: AnyRef](bean: T, typeHints: List[FieldMeta] = EmptyTypeHints): DataAdapter[T] =
    new DataAdapter[T](bean, typeHints)
}
