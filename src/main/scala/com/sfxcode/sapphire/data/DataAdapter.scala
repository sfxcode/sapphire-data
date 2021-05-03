package com.sfxcode.sapphire.data

import com.sfxcode.sapphire.data.el.{Expressions, ObjectExpressionHelper}
import com.sfxcode.sapphire.data.reflect.FieldMeta._
import com.sfxcode.sapphire.data.reflect.{FieldMeta, FieldRegistry, ReflectionTools}
import com.sfxcode.sapphire.data.wrapper.{FieldProperties, ValueHelper}
import com.typesafe.scalalogging.LazyLogging
import javafx.beans.property.Property
import javafx.beans.value.ObservableValue

import java.lang.reflect.Field
import java.util
import scala.collection.{immutable, mutable}
import scala.jdk.CollectionConverters._

class DataAdapter[T <: AnyRef](val wrappedData: T, typeHints: List[FieldMeta] = EmptyTypeHints)
    extends FieldProperties(typeHints)
    with ValueHelper
    with java.util.Map[String, Any]
    with LazyLogging {

  val reflectedFields: immutable.Map[String, Field] = FieldRegistry.fieldMap(wrappedData.getClass)

  def data: AnyRef = wrappedData

  def apply(key: String): Any =
    getValue(key)

  def getOldValue(key: String): Any =
    if (trackChanges)
      Some(changeManagementMap.get(key)).getOrElse(getValue(key))
    else
      getValue(key)

  private def handleRelations(key: String): Boolean =
    key.contains(".") && !key.contains(ObjectExpressionHelper.ExpressionPrefix) &&
    !key.contains(ObjectExpressionHelper.FxmlExpressionPrefix)

  def getValue(key: String): Any =
    wrappedData match {
      case map: scala.collection.Map[String, Any] => map(key)
      case javaMap: java.util.Map[String, Any]    => javaMap.get(key)
      case _ =>
        if (handleRelations(key)) {
          val objectKey = key.substring(0, key.indexOf("."))
          val newKey    = key.substring(key.indexOf(".") + 1)
          val value     = getValue(objectKey)
          val childBean = createChildForKey(objectKey, value)
          childBean.getValue(newKey)
        }
        else {
          try getValueForExpression(key).get
          catch {
            case e: Exception =>
              logger.warn(e.getMessage, e)
              null
          }
        }
    }

  def getValueForExpression[T <: Any](expression: String): Option[T] =
    Expressions.evaluateExpressionOnObject[T](wrappedData, expression)

  def updateValues(map: Map[String, Any]): Unit =
    map.keySet.foreach { key =>
      updateValue(key, map.get(key).orNull)
    }

  def updateValue(key: String, newValue: Any): Unit = {
    var valueToUpdate = newValue
    val property      = propertyMap.asScala.getOrElse(key, getProperty(key))

    if (newValue == None)
      valueToUpdate = null
    wrappedData match {
      case map: mutable.Map[String, Any]       => map.put(key, valueToUpdate)
      case javaMap: java.util.Map[String, Any] => javaMap.put(key, valueToUpdate)
      case _ =>
        if (handleRelations(key)) {
          val objectKey = key.substring(0, key.indexOf("."))
          val newKey    = key.substring(key.indexOf(".") + 1)
          val value     = getValue(objectKey)
          val childBean = createChildForKey(objectKey, value)
          childBean.updateValue(newKey, newValue)
        }
        else {
          ReflectionTools.setFieldValue(wrappedData, key, valueToUpdate)
          childrenMap.remove(key)
        }
    }
    updateObservableValue(property, valueToUpdate)
  }

  def changed(observable: ObservableValue[_], oldValue: Any, newValue: Any): Unit = {
    var key = ""
    observable match {
      case p: Property[_] => key = p.getName
      case _              =>
    }

    if (key.nonEmpty) {
      preserveChanges(key, oldValue, newValue)
      wrappedData match {
        case map: mutable.Map[String, Any]       => map.put(key, newValue)
        case javaMap: java.util.Map[String, Any] => javaMap.put(key, newValue)
        case _                                   => ReflectionTools.setFieldValue(wrappedData, key, newValue)
      }
    }

    expressionMap.keySet.asScala.foreach(k => updateObservableValue(expressionMap.get(k), getValue(k)))
    parentDataAdapter.foreach(bean => bean.childHasChanged(observable, oldValue, newValue))
  }

  def childHasChanged(observable: ObservableValue[_], oldValue: Any, newValue: Any): Unit =
    expressionMap.keySet.asScala.foreach(k => updateObservableValue(expressionMap.get(k), getValue(k)))

  def preserveChanges(key: String, oldValue: Any, newValue: Any): Unit =
    if (trackChanges) {
      if (changeManagementMap.containsKey(key)) {
        if (changeManagementMap.get(key) == newValue || newValue.equals(changeManagementMap.get(key)))
          changeManagementMap.remove(key)
      }
      else
        changeManagementMap.put(key, oldValue)
      hasChangesProperty.setValue(hasManagedChanges)
      updateParentChangesProperty()

    }

  def updateParentChangesProperty(): Unit =
    parentDataAdapter.foreach { adapter =>
      adapter.hasChangesProperty.setValue(parentDataAdapter.get.hasManagedChanges || hasManagedChanges)
      adapter.updateParentChangesProperty()
    }

  def revert(): Unit =
    if (trackChanges) {
      trackChanges = false
      changeManagementMap.asScala.keysIterator.foreach { key =>
        val oldValue = changeManagementMap.get(key)
        updateValue(key, oldValue)
      }
      childrenMap.keySet.foreach(key => childrenMap(key).revert())
      trackChanges = true
      clearChanges()
    }

  override def toString: String =
    "{%s : %s@%s}".format(super.toString, wrappedData, wrappedData.hashCode())

  // java.util.Map Fascade
  override def size(): Int =
    wrappedData match {
      case map: scala.collection.Map[_, _]     => map.size
      case javaMap: java.util.Map[String, Any] => javaMap.size()
      case _                                   => reflectedFields.size
    }

  override def isEmpty: Boolean =
    size() == 0

  override def containsKey(key: Any): Boolean =
    wrappedData match {
      case map: scala.collection.Map[String, _] => map.contains(key.toString)
      case javaMap: java.util.Map[String, Any]  => javaMap.containsKey(key)
      case _                                    => reflectedFields.keySet.contains(key.toString)
    }

  override def containsValue(value: Any): Boolean =
    wrappedData match {
      case map: scala.collection.Map[_, _]     => map.values.toList.contains(value)
      case javaMap: java.util.Map[String, Any] => javaMap.containsValue(value)
      case _                                   => FieldRegistry.memberMap(wrappedData).values.toList.contains(value)
    }

  override def get(key: Any): Any = getValue(key.toString)

  override def put(key: String, value: Any): Any = updateValue(key, value)

  override def remove(key: Any): Any =
    wrappedData match {
      case map: mutable.Map[String, Any]       => map.remove(key.toString)
      case javaMap: java.util.Map[String, Any] => javaMap.remove(key)
      case _                                   =>
    }

  override def putAll(map: java.util.Map[_ <: String, _]): Unit =
    map.keySet().asScala.foreach { key =>
      updateValue(key, map.get(key))
    }

  override def clear(): Unit =
    wrappedData match {
      case map: mutable.Map[String, Any]       => map.clear()
      case javaMap: java.util.Map[String, Any] => javaMap.clear()
      case _                                   =>
    }

  override def keySet(): java.util.Set[String] = wrappedData match {
    case map: scala.collection.Map[String, Any] => map.keySet.asJava
    case javaMap: java.util.Map[String, Any]    => javaMap.keySet
    case _                                      => FieldRegistry.memberMap(wrappedData).keySet.asJava
  }

  override def values(): java.util.Collection[Any] =
    wrappedData match {
      case map: scala.collection.Map[String, Any] => map.values.asJavaCollection
      case javaMap: java.util.Map[String, Any]    => javaMap.values()
      case _                                      => FieldRegistry.memberMap(wrappedData).values.asJavaCollection
    }

  override def entrySet(): java.util.Set[util.Map.Entry[String, Any]] =
    new java.util.HashSet[util.Map.Entry[String, Any]]()
}

object DataAdapter {

  def apply[T <: AnyRef](bean: T, typeHints: List[FieldMeta] = EmptyTypeHints): DataAdapter[T] =
    new DataAdapter[T](bean, typeHints)
}
