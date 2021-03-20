package com.sfxcode.sapphire.data

import com.typesafe.scalalogging.LazyLogging
import org.specs2.mutable._

class DataAdapterChildrenSpec extends Specification with LazyLogging {

  sequential

  "DataAdapter" should {

    "get child values" in {
      val testBean = DataAdapter[ParentBean](ParentBean())

      testBean.getValue("child.childName") must be equalTo "childName"

      testBean.getValue("child.address").toString must be equalTo "Address(oldStreet,12345)"

      testBean.getValue("child.address.street").toString must be equalTo "oldStreet"

    }

    "update child values" in {
      val testBean = DataAdapter[ParentBean](ParentBean())

      testBean.getValue("child.address.street").toString must be equalTo "oldStreet"

      testBean.updateValue("child.address.street", "newStreet")

      testBean.getValue("child.address.street").toString must be equalTo "newStreet"

      testBean.hasChanges must beTrue

    }

    "update child values with case class" in {
      val testBean = DataAdapter[ParentBean](ParentBean())

      testBean.getValue("child.address.street").toString must be equalTo "oldStreet"

      testBean.updateValue("child.address", Address("newStreet"))

      val street = testBean.wrappedData.child.address.street

      street must be equalTo "newStreet"

      testBean.getValue("child.address.street").toString must be equalTo "newStreet"

      testBean.hasChanges must beTrue

      testBean.updateValue("child", ChildBean("name2", Address("street2")))
      testBean.getValue("child.address.street").toString must be equalTo "street2"
      testBean.getValue("child.childName").toString must be equalTo "name2"

    }
  }

}
