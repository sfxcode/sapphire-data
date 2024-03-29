package com.sfxcode.sapphire.data.reflect

import java.lang.reflect.{ Field, ParameterizedType }
import java.time.LocalDate
import java.util.Date
import scala.collection.mutable

object PropertyType extends Enumeration {
  type PropertyValue = Value
  val TypeUnknown, TypeString, TypeInt, TypeLong, TypeFloat, TypeDouble, TypeBoolean, TypeLocalDate, TypeDate, TypeObject = Value
}

import com.sfxcode.sapphire.data.reflect.PropertyType._

object FieldMetaRegistry {

  private val metaMap = new mutable.HashMap[Class[_], mutable.HashMap[String, FieldMeta]]()

  def fieldMeta(target: AnyRef, name: String): FieldMeta = {
    val clazz = target.getClass
    if (!metaMap.contains(clazz))
      metaMap.put(clazz, new mutable.HashMap[String, FieldMeta]())
    val memberInfoMap = metaMap(clazz)
    if (memberInfoMap.contains(name) && (memberInfoMap(name).signature != TypeObject))
      return memberInfoMap(name)

    var result = FieldMeta(name)

    val fieldOption = FieldRegistry.field(target, name)
    if (fieldOption.isDefined) {
      val field = fieldOption.get

      val memberClazz = field.getType
      var optionClass: Class[_] = classOf[Any]
      val isOption = memberClazz.getName.contains("scala.Option")

      if (isOption) {
        optionClass = gusssOptionClass(field, target)
      }

      if (isOption && optionClass == classOf[String])
        result = FieldMeta(name, TypeString, isOption = true, Some(field))
      else if (memberClazz == classOf[String])
        result = FieldMeta(name, TypeString, isOption = false, Some(field))
      else if (isOption && (optionClass == classOf[Int] || optionClass.getName == "java.lang.Integer"))
        result = FieldMeta(name, TypeInt, isOption = true, Some(field))
      else if (memberClazz == classOf[Int])
        result = FieldMeta(name, TypeInt, isOption = false, Some(field))
      else if (isOption && (optionClass == classOf[Long] || optionClass.getName == "java.lang.Long"))
        result = FieldMeta(name, TypeLong, isOption = true, Some(field))
      else if (memberClazz == classOf[Long])
        result = FieldMeta(name, TypeLong, isOption = false, Some(field))
      else if (isOption && (optionClass == classOf[Float] || optionClass.getName == "java.lang.Float"))
        result = FieldMeta(name, TypeFloat, isOption = true, Some(field))
      else if (memberClazz == classOf[Float])
        result = FieldMeta(name, TypeFloat, isOption = false, Some(field))
      else if (isOption && (optionClass == classOf[Double] || optionClass.getName == "java.lang.Double"))
        result = FieldMeta(name, TypeDouble, isOption = true, Some(field))
      else if (memberClazz == classOf[Double])
        result = FieldMeta(name, TypeDouble, isOption = false, Some(field))
      else if (isOption && (optionClass == classOf[Boolean] || optionClass.getName == "java.lang.Boolean"))
        result = FieldMeta(name, TypeBoolean, isOption = true, Some(field))
      else if (memberClazz == classOf[Boolean])
        result = FieldMeta(name, TypeBoolean, isOption = false, Some(field))
      else if (isOption && optionClass == classOf[LocalDate])
        result = FieldMeta(name, TypeLocalDate, isOption = true, Some(field))
      else if (memberClazz == classOf[LocalDate])
        result = FieldMeta(name, TypeLocalDate, isOption = false, Some(field))
      else if (isOption && optionClass == classOf[Date])
        result = FieldMeta(name, TypeDate, isOption = true, Some(field))
      else if (memberClazz == classOf[Date])
        result = FieldMeta(name, TypeDate, isOption = false, Some(field))
      else if (isOption && optionClass == classOf[Any])
        result = FieldMeta(name, TypeObject, isOption = true, Some(field))
      else if (memberClazz == classOf[Any])
        result = FieldMeta(name, TypeObject, isOption = false, Some(field))
    }

    memberInfoMap.put(name, result)
    result
  }

  private def gusssOptionClass(field: Field, target: AnyRef): Class[_] = {
    var result: Class[_] = classOf[Any]
    field.setAccessible(true)
    val testValue = field.get(target)
    if (testValue != null && testValue.asInstanceOf[Option[_]].isDefined) {
      val optionValue = testValue.asInstanceOf[Option[_]].get
      result = optionValue.getClass
    }
    result
  }

}
