package com.sfxcode.sapphire.data

class DataAdapterValueHelperSpec extends munit.FunSuite {

  val adapter = DataAdapter[TestBean](TestBean())

  test("getValues from adapter") {

    assertEquals(adapter.intOption("age"), Some(42))
    assertEquals(adapter.longOption("age"), Some(42L))
    assertEquals(adapter.floatOption("age"), Some(42.0f))
    assertEquals(adapter.doubleOption("age"), Some(42.0))
    assertEquals(adapter.stringOption("age"), Some("42"))

    assertEquals(adapter.longOption("unknown"), None)
    assertEquals(adapter.longOption("unknown", Some(42)), Some(42L))
  }

}
