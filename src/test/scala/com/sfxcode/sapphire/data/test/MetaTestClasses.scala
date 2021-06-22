package com.sfxcode.sapphire.data.test

import java.time.LocalDate
import java.util.Date

case class StringTest(value: String = "myString", valueOption: Option[String] = Some("myString2"))

class StringTest2 {
  var value: String = "myString"
  var date: Date = new Date()
  var localdate: LocalDate = LocalDate.now()
  var valueOption: Option[String] = Some("myString2")

  def testMethod(a: Int): Int = a + 3
}

case class IntTest(value: Int = 11, valueOption: Option[Int] = Some(12))
case class LongTest(value: Long = 11, valueOption: Option[Long] = Some(12))
case class FloatTest(value: Float = 11.0f, valueOption: Option[Float] = Some(12.0f))
case class DoubleTest(value: Double = 11.0, valueOption: Option[Double] = Some(12.0))
case class BooleanTest(value: Boolean = false, valueOption: Option[Boolean] = Some(true))
case class DateTest(value: Date = new Date(), valueOption: Option[Date] = Some(new Date()))
case class LocalDateTest(value: LocalDate = LocalDate.now(), valueOption: Option[LocalDate] = Some(LocalDate.now()))

case class NoneTest(valueOption: Option[Long] = None)

object MetaTestClasses {

  val stringTest = StringTest()
  val stringTest2 = new StringTest2()
  val intTest = IntTest()
  val longTest = LongTest()
  val floatTest = FloatTest()
  val doubleTest = DoubleTest()
  val booleanTest = BooleanTest()
  val dateTest = DateTest()
  val localDateTest = LocalDateTest()
  val noneTest = NoneTest()

}
