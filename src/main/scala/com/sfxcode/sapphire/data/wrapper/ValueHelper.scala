package com.sfxcode.sapphire.data.wrapper

import com.typesafe.scalalogging.LazyLogging

import java.time.{ Instant, LocalDate, LocalDateTime, ZoneId }
import java.util.Date

trait ValueHelper extends LazyLogging {

  def getValue(key: String): Any

  def getValueOption(key: String, defaultValue: Option[Any] = None): Option[Any] =
    getValue(key) match {
      case v: Any => Some(v)
      case _ => defaultValue
    }

  def getStringValue(key: String, defaultValue: Option[String] = None): Option[String] =
    getValue(key) match {
      case s: String => Some(s)
      case v: Any => Some(v.toString)
      case _ => defaultValue
    }

  def getDateValue(key: String, defaultValue: Option[Date] = None): Option[Date] =
    getValue(key) match {
      case date: Date => Some(date)
      case localDate: LocalDate => Some(Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant))
      case _ => defaultValue
    }

  def getLocalDateValue(key: String, defaultValue: Option[LocalDate] = None): Option[LocalDate] =
    getValue(key) match {
      case localDate: LocalDate => Some(localDate)
      case date: Date => Some(Instant.ofEpochMilli(date.getTime).atZone(ZoneId.systemDefault()).toLocalDate)
      case _ => defaultValue
    }

  def getLocalDateTimeValue(key: String, defaultValue: Option[LocalDateTime] = None): Option[LocalDateTime] =
    getValue(key) match {
      case localDateTime: LocalDateTime => Some(localDateTime)
      case localDate: LocalDate => Some(localDate.atStartOfDay())
      case date: Date => Some(date.toInstant.atZone(ZoneId.systemDefault).toLocalDateTime)
      case _ => defaultValue
    }

  def getIntValue(key: String, defaultValue: Option[Int] = None): Option[Int] =
    getValue(key) match {
      case i: Int => Some(i)
      case n: Number => Some(n.intValue())
      case s: String =>
        try Some(s.toInt)
        catch {
          case _: Exception => defaultValue
        }
      case _ => defaultValue
    }

  def getLongValue(key: String, defaultValue: Option[Long] = None): Option[Long] =
    getValue(key) match {
      case l: Long => Some(l)
      case n: Number => Some(n.longValue())
      case s: String =>
        try Some(s.toLong)
        catch {
          case _: Exception => defaultValue
        }
      case _ => defaultValue
    }

  def getFloatValue(key: String, defaultValue: Option[Float] = None): Option[Float] =
    getValue(key) match {
      case f: Float => Some(f)
      case n: Number => Some(n.floatValue())
      case s: String =>
        try Some(s.toFloat)
        catch {
          case _: Exception => defaultValue
        }
      case _ => defaultValue
    }

  def getDoubleValue(key: String, defaultValue: Option[Double] = None): Option[Double] =
    getValue(key) match {
      case d: Double => Some(d)
      case n: Number => Some(n.doubleValue())
      case s: String =>
        try Some(s.toDouble)
        catch {
          case _: Exception => defaultValue
        }
      case _ => defaultValue
    }

}
