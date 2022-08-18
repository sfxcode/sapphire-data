package com.sfxcode.sapphire.data

import com.sfxcode.sapphire.data.test.TestJavaBean

case class UpdateTestBean(var name: String = "test", var age: Int = 42, var description: Option[String] = Some("desc"))

class DataAdapterUpdateSpec extends munit.FunSuite {

  test("update member value") {
    val testBean = DataAdapter(UpdateTestBean())
    testBean.updateValue("name", "new")
    assertEquals(testBean.value("name"), "new")
    assertEquals(testBean("name"), "new")
    assertEquals(testBean.oldValue("name"), "test")
    assert(testBean.hasChanges)
    testBean.updateValue("name", "test")
    assert(!testBean.hasChanges)
    testBean.updateValue("name", "new")
    assertEquals(testBean.value("name"), "new")
    testBean.revert()
    assertEquals(testBean.value("name"), "test")
  }

  test("update java member value") {
    val bean: TestJavaBean = new TestJavaBean()
    val testBean = DataAdapter(bean)

    testBean.updateValue("name", "new")
    assertEquals(testBean.value("name"), "new")
    assertEquals(testBean("name"), "new")
    assertEquals(testBean.oldValue("name"), "test")
    assert(testBean.hasChanges())
    testBean.updateValue("name", "test")
    assert(!testBean.hasChanges)

    testBean.updateValue("name", "new")
    assertEquals(testBean.value("name"), "new")
    testBean.revert()
    assertEquals(testBean.value("name"), "test")

    testBean.updateValue("age", 3)
    assertEquals(bean.getAge, 3)

  }

}
