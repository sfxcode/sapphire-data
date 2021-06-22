package com.sfxcode.sapphire.data

import com.sfxcode.sapphire.data.reflect.FieldMeta
import javafx.beans.property.StringProperty

import scala.collection.mutable

class DataAdapterMapSpec extends munit.FunSuite {

  test("update scala map value") {
    val testMap = new mutable.HashMap[String, Any]()
    testMap.put("name", "test")
    val testBean = DataAdapter(testMap, List(FieldMeta("name")))
    testBean.updateValue("name", "new")
    assertEquals(testBean.getValue("name"), "new")
    assertEquals(testMap("name"), "new")
    assertEquals(testBean("name"), "new")
    assertEquals(testBean.getOldValue("name"), "test")
    assert(testBean.hasChanges)
    testBean.updateValue("name", "test")
    assert(!testBean.hasChanges)
    testBean.updateValue("name", "new")
    assertEquals(testBean.getValue("name"), "new")
    testBean.revert()
    assertEquals(testBean.getValue("name"), "test")
    assertEquals(testBean("name"), "test")

    val property = testBean.getProperty("name")
    assert(property.isInstanceOf[StringProperty])
    property.asInstanceOf[StringProperty].setValue("ABC")
    assertEquals(testMap("name"), "ABC")
  }

  test("update java map value") {
    val testMap = new java.util.HashMap[String, Any]()
    testMap.put("name", "test")
    val wrapped = DataAdapter(testMap, List(FieldMeta("name")))
    wrapped.updateValue("name", "new")
    assertEquals(wrapped.getValue("name"), "new")
    assertEquals(testMap.get("name"), "new")
    assertEquals(wrapped("name"), "new")
    assertEquals(wrapped.getOldValue("name"), "test")
    assert(wrapped.hasChanges)
    wrapped.updateValue("name", "test")
    assert(!wrapped.hasChanges)
    wrapped.updateValue("name", "new")
    assertEquals(wrapped.getValue("name"), "new")
    wrapped.revert()
    assertEquals(wrapped.getValue("name"), "test")
    assertEquals(wrapped("name"), "test")

    val property = wrapped.getProperty("name")
    assert(property.isInstanceOf[StringProperty])
    property.asInstanceOf[StringProperty].setValue("ABC")
    assertEquals(testMap.get("name"), "ABC")
  }

}
