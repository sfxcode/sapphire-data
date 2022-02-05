package com.sfxcode.sapphire.data

class ConfigurationSpec extends munit.FunSuite with Configuration with Stage {

  test("config string") {
    assertEquals(configStringValue("com.sfxcode.sapphire.data.defaultDateConverterPattern"), "YYYY-MM-dd")
    assertEquals(configStringValue("test.string"), "Hello World")

    assertEquals(configStringValue("test.unknown"), "")
  }

  test("config string with stage") {
    assertEquals(configStringValue("test.__stage__.string"), "Development")
    setUnitTest()
    assertEquals(configStringValue("test.__stage__.string"), "UnitTest")
    setDevelopment()
    assertEquals(configStringValue("test.__stage__.string"), "Development")
  }

  test("config int list") {
    assertEquals(configIntValues("test.list.int"), List(1, 2, 3))
  }

}
