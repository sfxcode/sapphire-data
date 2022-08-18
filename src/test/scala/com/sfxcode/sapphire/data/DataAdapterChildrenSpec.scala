package com.sfxcode.sapphire.data

import com.typesafe.scalalogging.LazyLogging

class DataAdapterChildrenSpec extends munit.FunSuite with LazyLogging {

  test("get child values") {
    val adapter = DataAdapter[ParentBean](ParentBean())

    assertEquals(adapter.value("child.childName"), "childName")

    assertEquals(adapter.value("child.address").toString, "Address(oldStreet,12345)")

    assertEquals(adapter.value("child.address.street").toString, "oldStreet")

  }

  test("update child values") {
    val adapter = DataAdapter[ParentBean](ParentBean())

    assertEquals(adapter.value("child.address.street").toString, "oldStreet")

    adapter.updateValue("child.address.street", "newStreet")

    assertEquals(adapter.value("child.address.street").toString, "newStreet")

    assert(adapter.hasChanges())

  }

  test("update child values with case class") {
    val adapter = DataAdapter[ParentBean](ParentBean())

    assertEquals(adapter.value("child.address.street").toString, "oldStreet")

    adapter.updateValue("child.address", Address("newStreet"))

    val street = adapter.wrappedData.child.address.street

    assertEquals(street, "newStreet")

    val address = adapter.value("child.address").asInstanceOf[Address]

    assertEquals(address.street, "newStreet")

    assertEquals(adapter.value("child.address.street").toString, "newStreet")

    assert(adapter.hasChanges())

    adapter.updateValue("child", ChildBean("name2", Address("street2")))
    assertEquals(adapter.value("child.address.street").toString, "street2")
    assertEquals(adapter.value("child.childName").toString, "name2")

    assert(adapter.hasChanges())

  }

}
