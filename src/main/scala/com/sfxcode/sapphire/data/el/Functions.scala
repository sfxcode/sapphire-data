package com.sfxcode.sapphire.data.el

import com.typesafe.config.{ Config, ConfigFactory }

import java.text.SimpleDateFormat
import java.util.Date

object Functions {
  val config: Config = ConfigFactory.load()

  val SapphireFunctionPrefix = "sfx"

  val dateFormat = new SimpleDateFormat(config.getString("com.sfxcode.sapphire.data.defaultDateConverterPattern"));

  def addDefaultFunctions(helper: FunctionHelper): FunctionHelper = {
    val clazz: Class[_] = Class.forName("com.sfxcode.sapphire.data.el.Functions")
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
      classOf[String])
    helper.addFunction(
      SapphireFunctionPrefix,
      "format",
      classOf[java.lang.String],
      "format",
      classOf[String],
      classOf[Array[Any]])
    helper
  }

  def dataFrameworkVersion(): String = com.sfxcode.sapphire.data.BuildInfo.version

  def boolString(value: Boolean, trueValue: String, falseValue: String): String =
    if (value) {
      trueValue
    } else {
      falseValue
    }

  def now: Date = new Date

  def nowAsString: String = dateString(new java.util.Date)

  def dateString(date: AnyRef): String = {
    val s = date match {
      case d: java.util.Date => dateFormat.format(d)
      case c: java.util.Calendar => dateFormat.format(c.getTime)
      case c: javax.xml.datatype.XMLGregorianCalendar => dateFormat.format(c.toGregorianCalendar.getTime)
      case _ => "unknown date format"
    }
    s
  }
}
