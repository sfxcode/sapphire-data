package com.sfxcode.sapphire.data

import com.typesafe.scalalogging.LazyLogging
import org.specs2.mutable._

class DataAdapterChildrenSpec extends Specification with LazyLogging {

  sequential

  "DataAdapter" should {

    "get child values" in {
      val testBean = DataAdapter[ParentBean](ParentBean())

      testBean.getValue("child.childName") must be equalTo "childName"

      testBean.getValue("child.address").toString must be equalTo "Address(street,12345)"

      testBean.getValue("child.address.street").toString must be equalTo "street"

    }

    "update child values" in {
      val testBean = DataAdapter[ParentBean](ParentBean())

      testBean.getValue("child.address.street").toString must be equalTo "street"

      testBean.updateValue("child.address.street", "newStreet")

      testBean.getValue("child.address.street").toString must be equalTo "newStreet"

      testBean.hasChanges must beTrue

    }
  }

}
