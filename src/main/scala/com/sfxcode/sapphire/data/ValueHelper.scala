package com.sfxcode.sapphire.data

import com.typesafe.scalalogging.LazyLogging

import java.time.{ Instant, LocalDate, LocalDateTime, ZoneId }
import java.util.Date

trait ValueHelper extends LazyLogging {

  def value(key: String): Any
  def oldValue(key: String): Any

  def valueOption(key: String, defaultValue: Option[Any] = None): Option[Any] =
    value(key) match {
      case v: Any => Some(v)
      case _ => defaultValue
    }

  def stringOption(key: String, defaultValue: Option[String] = None): Option[String] =
    value(key) match {
      case s: String => Some(s)
      case v: Any => Some(v.toString)
      case _ => defaultValue
    }

  def stringValue(key: String): String =
    stringOption(key, Some("")).get

  def dateOption(key: String, defaultValue: Option[Date] = None): Option[Date] =
    value(key) match {
      case date: Date => Some(date)
      case localDate: LocalDate => Some(Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant))
      case _ => defaultValue
    }

  def dateValue(key: String): Date =
    dateOption(key).orNull

  def localDateOption(key: String, defaultValue: Option[LocalDate] = None): Option[LocalDate] =
    value(key) match {
      case localDate: LocalDate => Some(localDate)
      case date: Date => Some(Instant.ofEpochMilli(date.getTime).atZone(ZoneId.systemDefault()).toLocalDate)
      case _ => defaultValue
    }

  def localDateValue(key: String): LocalDate =
    localDateOption(key).orNull

  def localDateTimeOption(key: String, defaultValue: Option[LocalDateTime] = None): Option[LocalDateTime] =
    value(key) match {
      case localDateTime: LocalDateTime => Some(localDateTime)
      case localDate: LocalDate => Some(localDate.atStartOfDay())
      case date: Date => Some(date.toInstant.atZone(ZoneId.systemDefault).toLocalDateTime)
      case _ => defaultValue
    }

  def localDateTimeValue(key: String): LocalDateTime =
    localDateTimeOption(key).orNull

  def intOption(key: String, defaultValue: Option[Int] = None): Option[Int] =
    value(key) match {
      case i: Int => Some(i)
      case n: Number => Some(n.intValue())
      case s: String =>
        try Some(s.toInt)
        catch {
          case _: Exception => defaultValue
        }
      case _ => defaultValue
    }

  def intValue(key: String): Int =
    intOption(key, Some(0)).get

  def longOption(key: String, defaultValue: Option[Long] = None): Option[Long] =
    value(key) match {
      case l: Long => Some(l)
      case n: Number => Some(n.longValue())
      case s: String =>
        try Some(s.toLong)
        catch {
          case _: Exception => defaultValue
        }
      case _ => defaultValue
    }

  def longValue(key: String): Long =
    longOption(key, Some(0)).get

  def floatOption(key: String, defaultValue: Option[Float] = None): Option[Float] =
    value(key) match {
      case f: Float => Some(f)
      case n: Number => Some(n.floatValue())
      case s: String =>
        try Some(s.toFloat)
        catch {
          case _: Exception => defaultValue
        }
      case _ => defaultValue
    }

  def floatValue(key: String): Float =
    floatOption(key, Some(0)).get

  def doubleOption(key: String, defaultValue: Option[Double] = None): Option[Double] =
    value(key) match {
      case d: Double => Some(d)
      case n: Number => Some(n.doubleValue())
      case s: String =>
        try Some(s.toDouble)
        catch {
          case _: Exception => defaultValue
        }
      case _ => defaultValue
    }

  def doubleValue(key: String): Double =
    doubleOption(key, Some(0)).get

  def booleanOption(key: String, defaultValue: Option[Boolean] = None): Option[Boolean] =
    value(key) match {
      case d: Boolean => Some(d)
      case s: String => Some(s.toBoolean)
      case _ => defaultValue
    }

  def booleanValue(key: String): Boolean =
    booleanOption(key, Some(false)).get

}
