package com.sfxcode.sapphire.data

import com.sfxcode.sapphire.data.reflect.FieldMeta

import scala.collection.mutable

class DataAdapterMapSpec extends munit.FunSuite {

  test("update scala map value") {
    val testMap = new mutable.HashMap[String, Any]()
    testMap.put("name", "test")
    val testBean = DataAdapter(testMap, List(FieldMeta("name")))
    testBean.updateValue("name", "new")
    assertEquals(testBean.value("name"), "new")
    assertEquals(testMap("name"), "new")
    assertEquals(testBean("name"), "new")
    assertEquals(testBean.oldValue("name"), "test")
    assert(testBean.hasChanges())
    testBean.updateValue("name", "test")
    assert(!testBean.hasChanges())
    testBean.updateValue("name", "new")
    assertEquals(testBean.value("name"), "new")
    testBean.revert()
    assertEquals(testBean.value("name"), "test")
    assertEquals(testBean("name"), "test")

    assertEquals(testMap("name"), "test")
  }

  test("update java map value") {
    val testMap = new java.util.HashMap[String, Any]()
    testMap.put("name", "test")
    val wrapped = DataAdapter(testMap, List(FieldMeta("name")))
    wrapped.updateValue("name", "new")
    assertEquals(wrapped.value("name"), "new")
    assertEquals(testMap.get("name"), "new")
    assertEquals(wrapped("name"), "new")
    assertEquals(wrapped.oldValue("name"), "test")
    assert(wrapped.hasChanges())
    wrapped.updateValue("name", "test")
    assert(!wrapped.hasChanges)
    wrapped.updateValue("name", "new")
    assertEquals(wrapped.value("name"), "new")
    wrapped.revert()
    assertEquals(wrapped.value("name"), "test")
    assertEquals(wrapped("name"), "test")

    assertEquals(testMap.get("name"), "test")
  }

}
