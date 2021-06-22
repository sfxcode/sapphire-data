package com.sfxcode.sapphire.data.el

object CustomFunctionMapper {
  def coolMethod(s: String): String = "test-" + s
}

class ExpressionsSpec extends munit.FunSuite {

  test("registerValues") {
    Expressions.register("DefaultFunctionsSpec", "Test")
    assertEquals(Expressions.getValue("${DefaultFunctionsSpec}").get, "Test")
  }

  // #customFunction
  test("add custom function") {

    val clazz: Class[_] = Class.forName("com.sfxcode.sapphire.data.el.CustomFunctionMapper")
    Expressions.functionHelper.addFunction("custom", "myCoolMethod", clazz, "coolMethod", classOf[String])
    assertEquals(Expressions.getValue("${custom:myCoolMethod('123')}").get, "test-123")

  }
  // #customFunction

}
