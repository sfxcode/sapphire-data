package com.sfxcode.sapphire.data.wrapper

import com.sfxcode.sapphire.data.el.ObjectExpressionHelper
import com.sfxcode.sapphire.data.reflect.PropertyType._
import com.sfxcode.sapphire.data.reflect.{ FieldMeta, FieldMetaRegistry }
import com.sfxcode.sapphire.data.wrapper.FieldProperties.defaultDateConverter
import com.sfxcode.sapphire.data.{ Configuration, DataAdapter }
import javafx.beans.property._
import javafx.beans.value.ChangeListener
import javafx.collections.{ FXCollections, ObservableMap }
import javafx.util.converter.{ DateStringConverter, DateTimeStringConverter }

import java.lang
import java.time.LocalDate
import scala.collection.mutable

abstract class FieldProperties(val typeHints: List[FieldMeta]) extends ChangeListener[Any] {
  lazy val changeManagementMap: ObservableMap[String, Any] = FXCollections.observableHashMap[String, Any]()
  lazy val hasChangesProperty = new SimpleBooleanProperty(data, "_hasChanges", false)
  lazy val expressionMap: ObservableMap[String, Property[_]] = FXCollections.observableHashMap[String, Property[_]]()
  lazy val propertyMap: ObservableMap[String, Property[_]] = FXCollections.observableHashMap[String, Property[_]]()

  val childrenMap = new mutable.HashMap[String, DataAdapter[AnyRef]]
  val EmptyMemberInfo: FieldMeta = FieldMeta("name_ignored")
  val memberInfoMap: Map[String, FieldMeta] = typeHints.map(info => (info.name, info)).toMap
  var parentDataAdapter: Option[DataAdapter[AnyRef]] = None
  var trackChanges = true

  def data: AnyRef

  def getValue(key: String): Any

  def hasChanges: Boolean = hasChangesProperty.get()

  def memberInfo(name: String): FieldMeta =
    memberInfoMap.getOrElse(name, EmptyMemberInfo)

  // property
  def getStringProperty(key: String): StringProperty = getProperty(key).asInstanceOf[StringProperty]

  def getBooleanProperty(key: String): BooleanProperty = getProperty(key).asInstanceOf[BooleanProperty]

  def getIntegerProperty(key: String): IntegerProperty = getProperty(key).asInstanceOf[IntegerProperty]

  def getLongProperty(key: String): LongProperty = getProperty(key).asInstanceOf[LongProperty]

  def getFloatProperty(key: String): FloatProperty = getProperty(key).asInstanceOf[FloatProperty]

  def getDoubleProperty(key: String): DoubleProperty = getProperty(key).asInstanceOf[DoubleProperty]

  def getObjectProperty[S <: Any](key: String): ObjectProperty[S] = getProperty(key).asInstanceOf[ObjectProperty[S]]

  def getProperty(key: String): Property[_] =
    if (key.contains(".") && !ObjectExpressionHelper.isExpressionKey(key)) {
      val objectKey = key.substring(0, key.indexOf("."))
      val newKey = key.substring(key.indexOf(".") + 1)
      val value = getValue(objectKey)
      val childBean = createChildForKey(objectKey, value)
      childBean.getProperty(newKey)
    } else {
      if ("_hasChanges".equals(key)) {
        return hasChangesProperty
      }
      var value = getValue(key)
      value match {
        case option: Option[_] =>
          if (option.isDefined)
            value = option.get
          else
            value = null
        case _ =>
      }
      value match {
        case value1: Property[_] => value1
        case _ =>
          // lookup in local function
          var info = memberInfo(key)
          if (info.signature == TypeUnknown)
            // lookup in registry
            data match {
              case map: mutable.Map[String, Any] =>
              case javaMap: java.util.Map[String, Any] =>
              case _ => info = FieldMetaRegistry.fieldMeta(data, key)
            }

          if (info.signature != TypeUnknown)
            if (propertyMap.containsKey(key))
              propertyMap.get(key)
            else {
              var result: Any = null
              info.signature match {
                case TypeInt => result = new SimpleIntegerProperty(data, info.name, value.asInstanceOf[Integer])
                case TypeLong => result = new SimpleLongProperty(data, info.name, value.asInstanceOf[Long])
                case TypeFloat => result = new SimpleFloatProperty(data, info.name, value.asInstanceOf[Float])
                case TypeDouble => result = new SimpleDoubleProperty(data, info.name, value.asInstanceOf[Double])
                case TypeBoolean => result = new SimpleBooleanProperty(data, info.name, value.asInstanceOf[Boolean])
                case TypeLocalDate =>
                  result = new SimpleObjectProperty(data, info.name, value.asInstanceOf[LocalDate])
                case _ => result = createPropertyForObject(value, info.name)
              }

              val property = result.asInstanceOf[Property[_]]
              property.addListener(this)
              propertyMap.put(key, property)
              property
            }
          else if (expressionMap.containsKey(key))
            expressionMap.get(key)
          else {
            var result: Any = null

            value match {
              case i: Integer => result = new SimpleIntegerProperty(data, info.name, i)
              case l: Long => result = new SimpleLongProperty(data, info.name, l)
              case f: Float => result = new SimpleFloatProperty(data, info.name, f)
              case d: Double => result = new SimpleDoubleProperty(data, info.name, d)
              case b: Boolean => result = new SimpleBooleanProperty(data, info.name, b)
              case ld: LocalDate => result = new SimpleObjectProperty(data, info.name, ld)
              case _ => result = createPropertyForObject(value, info.name)
            }

            val property = result.asInstanceOf[Property[_]]
            property.addListener(this)
            expressionMap.put(key, property)
            property
          }
      }
    }

  protected def updateObservableValue(property: Property[_], value: Any): Unit =
    property match {
      case s: StringProperty => updateStringProperty(s, value)

      case i: IntegerProperty => updateIntProperty(i, value)
      case l: LongProperty => updateLongProperty(l, value)
      case f: FloatProperty => updateFloatProperty(f, value)
      case d: DoubleProperty => updateDoubleProperty(d, value)
      case b: BooleanProperty => updateBooleanProperty(b, value)
      case o: ObjectProperty[Any] => o.set(value)
      case _ =>
    }

  protected def updateStringProperty(property: StringProperty, value: Any): Unit =
    value match {
      case s: String => property.set(s)
      case d: java.util.Date => property.set(defaultDateConverter.toString(d))
      case c: java.util.Calendar => property.set(defaultDateConverter.toString(c.getTime))
      case c: javax.xml.datatype.XMLGregorianCalendar =>
        property.set(defaultDateConverter.toString(c.toGregorianCalendar.getTime))
      case _ =>
        if (value != null) {
          property.set(value.toString)
        } else {
          property.set(null)
        }
    }

  protected def updateIntProperty(property: IntegerProperty, value: Any): Unit =
    value match {
      case n: Number => property.set(n.intValue())
      case s: String if s.nonEmpty =>
        property.set(value.toString.toInt)
      case _ =>
    }

  protected def updateLongProperty(property: LongProperty, value: Any): Unit =
    value match {
      case n: Number => property.set(n.longValue())
      case s: String if s.nonEmpty =>
        property.set(value.toString.toLong)
      case _ =>
    }

  protected def updateDoubleProperty(property: DoubleProperty, value: Any): Unit =
    value match {
      case n: Number => property.set(n.doubleValue())
      case s: String if s.nonEmpty =>
        property.set(value.toString.toDouble)
      case _ =>
    }

  protected def updateFloatProperty(property: FloatProperty, value: Any): Unit =
    value match {
      case n: Number => property.set(n.floatValue())
      case s: String if s.nonEmpty =>
        property.set(value.toString.toFloat)
      case _ =>
    }

  protected def updateBooleanProperty(property: BooleanProperty, value: Any): Unit =
    value match {
      case b: Boolean => property.set(b)
      case s: String if s.nonEmpty =>
        property.set(java.lang.Boolean.valueOf(s))
      case _ =>
    }

  def hasManagedChanges: lang.Boolean = {
    val childrenChangeCount: Int = childrenMap.values.map(bean => bean.changeManagementMap.size).sum
    (changeManagementMap.size + childrenChangeCount) > 0
  }

  def clearChanges(): Unit =
    if (trackChanges) {
      changeManagementMap.clear()
      hasChangesProperty.setValue(hasManagedChanges)
      childrenMap.values.foreach(_.clearChanges())
    }

  protected def createPropertyForObject(value: Any, name: String): Any = {
    val propertyValue: Any = value match {
      case d: java.util.Date => defaultDateConverter.toString(d)
      case c: java.util.Calendar => defaultDateConverter.toString(c.getTime)
      case c: javax.xml.datatype.XMLGregorianCalendar =>
        defaultDateConverter.toString(c.toGregorianCalendar.getTime)
      case v: AnyRef => v
      case _ => ""
    }
    propertyValue match {
      case s: String => new SimpleStringProperty(data, name, s)
      case v: AnyRef => new SimpleObjectProperty(data, name, v)
      case _ => new SimpleStringProperty(data, name, "s")
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

}

object FieldProperties extends Configuration {

  val DefaultDateConverterPattern: String = configStringValue("com.sfxcode.sapphire.data.defaultDateConverterPattern")
  val DefaultDateTimeConverterPattern: String = configStringValue(
    "com.sfxcode.sapphire.data.defaultDateTimeConverterPattern")

  var defaultDateConverter = new DateStringConverter(DefaultDateConverterPattern)
  var defaultDateTimeConverter = new DateTimeStringConverter(DefaultDateTimeConverterPattern)
}
