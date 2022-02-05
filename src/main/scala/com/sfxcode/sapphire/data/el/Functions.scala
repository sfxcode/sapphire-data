package com.sfxcode.sapphire.data.el

import com.sfxcode.sapphire.data.Configuration
import com.sfxcode.sapphire.data.wrapper.FieldProperties.defaultDateConverter

import java.util.Date

object Functions extends Configuration {
  val SapphireFunctionPrefix = "sfx"

  def addDefaultFunctions(helper: FunctionHelper): FunctionHelper = {
    val clazz: Class[_] = Class.forName("com.sfxcode.sapphire.data.el.DefaultFunctions")
    helper.addFunction(SapphireFunctionPrefix, "dataFrameworkVersion", clazz, "dataFrameworkVersion")
    helper.addFunction(SapphireFunctionPrefix, "dateString", clazz, "dateString", classOf[AnyRef])
    helper.addFunction(SapphireFunctionPrefix, "now", clazz, "now")
    helper.addFunction(SapphireFunctionPrefix, "nowAsString", clazz, "nowAsString")
    helper.addFunction(
      SapphireFunctionPrefix,
      "boolString",
      clazz,
      "boolString",
      classOf[Boolean],
      classOf[String],
      classOf[String]
    )
    helper.addFunction(SapphireFunctionPrefix, "configString", clazz, "configString", classOf[String])
    helper.addFunction(
      SapphireFunctionPrefix,
      "format",
      classOf[java.lang.String],
      "format",
      classOf[String],
      classOf[Array[Any]]
    )
    helper
  }

  def dataFrameworkVersion(): String = com.sfxcode.sapphire.data.BuildInfo.version

  def boolString(value: Boolean, trueValue: String, falseValue: String): String =
    if (value) {
      trueValue
    }
    else {
      falseValue
    }

  def now: Date = new Date

  def nowAsString: String = dateString(new java.util.Date)

  def dateString(date: AnyRef): String = {
    val s = date match {
      case d: java.util.Date     => defaultDateConverter.toString(d)
      case c: java.util.Calendar => defaultDateConverter.toString(c.getTime)
      case c: javax.xml.datatype.XMLGregorianCalendar =>
        defaultDateConverter.toString(c.toGregorianCalendar.getTime)
      case _ => "unknown date format"
    }
    s
  }

  def configString(path: String): String = configStringValue(path)

}
