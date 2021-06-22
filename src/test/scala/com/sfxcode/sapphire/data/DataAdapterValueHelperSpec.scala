package com.sfxcode.sapphire.data

class DataAdapterValueHelperSpec extends munit.FunSuite {

  val adapter = DataAdapter[TestBean](TestBean())

  test("getValues from adapter") {

    assertEquals(adapter.getIntValue("age"), Some(42))
    assertEquals(adapter.getLongValue("age"), Some(42L))
    assertEquals(adapter.getFloatValue("age"), Some(42.0f))
    assertEquals(adapter.getDoubleValue("age"), Some(42.0))
    assertEquals(adapter.getStringValue("age"), Some("42"))

    assertEquals(adapter.getLongValue("unknown"), None)
    assertEquals(adapter.getLongValue("unknown", Some(42)), Some(42L))
  }

}
