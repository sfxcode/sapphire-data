package com.sfxcode.sapphire.data

import com.typesafe.scalalogging.LazyLogging
import org.specs2.mutable._

class DataAdapterValueHelperSpec extends Specification with LazyLogging {

  sequential
  val adapter = DataAdapter[TestBean](TestBean())

  "ValueHelper" should {

    "get getValues " in {

      adapter.getIntValue("age") must beSome(42)
      adapter.getLongValue("age") must beSome(42L)
      adapter.getFloatValue("age") must beSome(42.0f)
      adapter.getDoubleValue("age") must beSome(42.0)
      adapter.getStringValue("age") must beSome("42")

      adapter.getLongValue("unknown") must beNone
      adapter.getLongValue("unknown", Some(42)) must beSome(42L)

    }
  }

}
