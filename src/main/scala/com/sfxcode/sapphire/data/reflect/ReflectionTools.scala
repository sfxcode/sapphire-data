package com.sfxcode.sapphire.data.reflect

import com.sfxcode.sapphire.data.DataAdapter
import com.sfxcode.sapphire.data.el.Functions.config
import com.typesafe.scalalogging.LazyLogging

import java.time.LocalDate
import java.util.Date

object ReflectionTools extends LazyLogging {
  private val MapKey = "key"
  val useAutoConversion = config.getBoolean("com.sfxcode.sapphire.data.useAutoConversion")
  def getFieldValue(target: Any, name: String): Any = {
    val fieldMeta = FieldMetaRegistry.fieldMeta(target.asInstanceOf[AnyRef], name)
    val field = fieldMeta.field.get
    field.setAccessible(true)
    field.get(target)
  }

  def setFieldValue(target: Any, name: String, value: Any): Unit = {
    val fieldMeta = FieldMetaRegistry.fieldMeta(target.asInstanceOf[AnyRef], name)
    val fieldOption = fieldMeta.field

    if (fieldOption.isDefined) {
      val field = fieldOption.get
      val valueToUpdate = convertValueForFieldMeta(fieldMeta, value)

      try {
        field.setAccessible(true)

        if (fieldMeta.isOption) {
          if (valueToUpdate == null) {
            field.set(target, None)
          } else if (valueToUpdate.isInstanceOf[Option[_]]) {
            field.set(target, valueToUpdate)
          } else {
            field.set(target, Some(valueToUpdate))
          }
        } else {
          field.set(target, valueToUpdate)
        }
      } catch {
        case exc: Exception =>
          logger.debug("can not update %s for field %s - %s".format(valueToUpdate, name, exc.getMessage))
      }
    } else {
      try {
        val field = target.getClass.getDeclaredField(name)
        field.setAccessible(true)
        field.set(target, value)
        field
      } catch {
        case exc: Exception =>
          logger.error("can not update %s for field %s - %s".format(value, name, exc.getMessage))
      }
    }
  }

  def convertValueForFieldMeta(fieldMeta: FieldMeta, value: Any): Any = {
    var result = value

    if (useAutoConversion) {

      if (value != null && !fieldMeta.isOption) {
        if (fieldMeta.signature == PropertyType.TypeInt && !value.isInstanceOf[Int]) {
          result = DataAdapter(Map(MapKey -> value)).intValue(MapKey)
        } else if (fieldMeta.signature == PropertyType.TypeLong && !value.isInstanceOf[Int]) {
          result = DataAdapter(Map(MapKey -> value)).longValue(MapKey)
        } else if (fieldMeta.signature == PropertyType.TypeFloat && !value.isInstanceOf[Int]) {
          result = DataAdapter(Map(MapKey -> value)).floatValue(MapKey)
        } else if (fieldMeta.signature == PropertyType.TypeDouble && !value.isInstanceOf[Int]) {
          result = DataAdapter(Map(MapKey -> value)).doubleValue(MapKey)
        } else if (fieldMeta.signature == PropertyType.TypeString && !value.isInstanceOf[String]) {
          result = value.toString
        } else if (fieldMeta.signature == PropertyType.TypeBoolean && !value.isInstanceOf[Boolean]) {
          result = DataAdapter(Map(MapKey -> value)).booleanValue(MapKey)
        } else if (fieldMeta.signature == PropertyType.TypeDate && !value.isInstanceOf[Date]) {
          result = DataAdapter(Map(MapKey -> value)).dateValue(MapKey)
        } else if (fieldMeta.signature == PropertyType.TypeLocalDate && !value.isInstanceOf[LocalDate]) {
          result = DataAdapter(Map(MapKey -> value)).localDateValue(MapKey)
        }
      }
    }

    result
  }

}
