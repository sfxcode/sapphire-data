package com.sfxcode.sapphire.data

import com.typesafe.scalalogging.LazyLogging
import org.specs2.mutable._

class DataAdapterChildrenSpec extends Specification with LazyLogging {

  sequential

  "DataAdapter" should {

    "get child values" in {
      val adapter = DataAdapter[ParentBean](ParentBean())

      adapter.getValue("child.childName") must be equalTo "childName"

      adapter.getValue("child.address").toString must be equalTo "Address(oldStreet,12345)"

      adapter.getValue("child.address.street").toString must be equalTo "oldStreet"

    }

    "update child values" in {
      val adapter = DataAdapter[ParentBean](ParentBean())

      adapter.getValue("child.address.street").toString must be equalTo "oldStreet"

      adapter.updateValue("child.address.street", "newStreet")

      adapter.getValue("child.address.street").toString must be equalTo "newStreet"

      adapter.hasChanges must beTrue

    }

    "update child values with case class" in {
      val adapter = DataAdapter[ParentBean](ParentBean())

      adapter.getValue("child.address.street").toString must be equalTo "oldStreet"

      adapter.updateValue("child.address", Address("newStreet"))

      val street = adapter.wrappedData.child.address.street

      street must be equalTo "newStreet"

      val address = adapter.getValue("child.address").asInstanceOf[Address]

      address.street must be equalTo "newStreet"

      adapter.getValue("child.address.street").toString must be equalTo "newStreet"

      adapter.hasChanges must beTrue

      adapter.updateValue("child", ChildBean("name2", Address("street2")))
      adapter.getValue("child.address.street").toString must be equalTo "street2"
      adapter.getValue("child.childName").toString must be equalTo "name2"

      adapter.hasChanges must beTrue

    }
  }

}
