package com.sfxcode.sapphire.data.reflect

import com.typesafe.scalalogging.LazyLogging

object ReflectionTools extends LazyLogging {

  def getFieldValue(target: Any, name: String): Any = {
    val fieldMeta = FieldMetaRegistry.fieldMeta(target.asInstanceOf[AnyRef], name)
    val field     = fieldMeta.field.get
    field.setAccessible(true)
    field.get(target)
  }

  def setFieldValue(target: Any, name: String, value: Any): Unit = {
    val fieldMeta   = FieldMetaRegistry.fieldMeta(target.asInstanceOf[AnyRef], name)
    val fieldOption = fieldMeta.field

    if (fieldOption.isDefined) {
      val field = fieldOption.get
      try {
        field.setAccessible(true)

        if (fieldMeta.isOption)
          if (value == null)
            field.set(target, None)
          else if (value.isInstanceOf[Option[_]])
            field.set(target, value)
          else
            field.set(target, Some(value))
        else
          field.set(target, value)
      }
      catch {
        case exc: Exception =>
          logger.debug("can not update %s for field %s - %s".format(value, name, exc.getMessage))
      }
    }
    else {
      try {
        val field = target.getClass.getDeclaredField(name)
        field.setAccessible(true)
        field.set(target, value)
        field
      }
      catch {
        case exc: Exception =>
          logger.error("can not update %s for field %s - %s".format(value, name, exc.getMessage))
      }
    }

  }

}
