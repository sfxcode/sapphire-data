package com.sfxcode.sapphire.data.reflect

import com.sfxcode.sapphire.data.test.MetaTestClasses._

import com.sfxcode.sapphire.data.reflect.FieldMetaRegistry._
import com.sfxcode.sapphire.data.reflect.PropertyType._

class FieldMetaRegistrySpec extends munit.FunSuite {

  def assertFieldMeta(target: AnyRef, name: String, signature: PropertyValue, isOption: Boolean): Boolean = {
    val info = fieldMeta(target, name)
    assertEquals(info.name, name)
    assertEquals(info.signature, signature)
    assertEquals(info.isOption, isOption)
    true

  }

  test("get member info for String") {

    assertFieldMeta(stringTest, "value", TypeString, isOption = false)
    assertFieldMeta(stringTest, "valueOption", TypeString, isOption = true)

    assertFieldMeta(stringTest2, "value", TypeString, isOption = false)
    assertFieldMeta(stringTest2, "valueOption", TypeString, isOption = true)

  }

  test("get member info for Int") {

    assertFieldMeta(intTest, "value", TypeInt, isOption = false)
    assertFieldMeta(intTest, "valueOption", TypeInt, isOption = true)

  }

  test("get member info for Long") {
    assertFieldMeta(longTest, "value", TypeLong, isOption = false)
    assertFieldMeta(longTest, "valueOption", TypeLong, isOption = true)

  }

  test("get member info for Long with None") {

    assertFieldMeta(noneTest, "valueOption", TypeObject, isOption = true)
  }

  test("get member info for Float") {

    assertFieldMeta(floatTest, "value", TypeFloat, isOption = false)
    assertFieldMeta(floatTest, "valueOption", TypeFloat, isOption = true)

  }

  test("get member info for Double") {

    assertFieldMeta(doubleTest, "value", TypeDouble, isOption = false)
    assertFieldMeta(doubleTest, "valueOption", TypeDouble, isOption = true)

  }

  test("get member info for Boolean") {

    assertFieldMeta(booleanTest, "value", TypeBoolean, isOption = false)
    assertFieldMeta(booleanTest, "valueOption", TypeBoolean, isOption = true)

  }

  test("get member info for Date") {

    assertFieldMeta(dateTest, "value", TypeDate, isOption = false)
    assertFieldMeta(dateTest, "valueOption", TypeDate, isOption = true)

  }

  test("get member info for LocalDate") {

    assertFieldMeta(localDateTest, "value", TypeLocalDate, isOption = false)
    assertFieldMeta(localDateTest, "valueOption", TypeLocalDate, isOption = true)
  }

  test("get member info performance") {
    // warmup and cache
    assertFieldMeta(stringTest, "value", TypeString, isOption = false)
    assertFieldMeta(stringTest, "valueOption", TypeString, isOption = true)

    val start = System.currentTimeMillis()
    (1 to 10000).foreach { _ =>
      assertFieldMeta(stringTest, "value", TypeString, isOption = false)
      assertFieldMeta(stringTest, "valueOption", TypeString, isOption = true)
    }
    val time = System.currentTimeMillis() - start

    assert(time < 2000)
  }

}
