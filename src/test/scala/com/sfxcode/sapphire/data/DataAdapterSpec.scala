package com.sfxcode.sapphire.data

import com.sfxcode.sapphire.data.test.TestJavaBean
import com.typesafe.scalalogging.LazyLogging

case class Author(var name: String)

case class Book(id: Long, title: String, pages: Int, author: Author)

case class Zip(value: Long = 12345)

case class TestBean(name: String = "test", age: Int = 42, zip: Zip = Zip(), description: Option[String] = Some("desc")) {
  def doubleAge(): Int = age * 2

  def multiply(first: java.lang.Long, second: java.lang.Long): Long = first * second

}

class TestClass(var name: String = "test", var age: Int = 42, var zip: Zip = Zip(), var description: Option[String] = Some("desc")) {
  def doubleAge(): Int = age * 2

  def multiply(first: java.lang.Long, second: java.lang.Long): Long = first * second

}

case class Address(street: String = "oldStreet", postal: String = "12345")

case class ChildBean(childName: String = "childName", address: Address = Address()) {
  def fullChildName(): String = "[child] %s".format(childName)
}

case class ParentBean(parentName: String = "parentName", child: ChildBean = ChildBean()) {
  def fullName(): String = "%s : %s".format(parentName, child.fullChildName())
}

case class ListClass(list: List[String] = List("A", "B"))

class DataAdapterSpec extends munit.FunSuite with LazyLogging {

  test("get value of members of case class") {

    val testBean = DataAdapter[TestBean](TestBean())
    assertEquals(testBean.value("name"), "test")
    assertEquals(testBean.value("age"), 42)
    assertEquals(testBean.value("description"), Some("desc"))
    assertEquals(testBean.value("zip").asInstanceOf[Zip].value, 12345L)

    assertEquals(testBean("name"), "test")
    assertEquals(testBean("age"), 42)
    assertEquals(testBean("description"), Some("desc"))
    assertEquals(testBean("zip").asInstanceOf[Zip].value, 12345L)

    val testBean2 = DataAdapter[TestBean](TestBean())

    testBean2.updateValue("description", None)
    assertEquals(testBean2.data.description, None)
    assertEquals(testBean2.value("description"), None)

  }

  test("get value of members of class") {
    val testBean = DataAdapter[TestClass](new TestClass())
    assertEquals(testBean.value("name"), "test")
    assertEquals(testBean.value("age"), 42)
    assertEquals(testBean.value("description"), Some("desc"))
    assertEquals(testBean.value("zip").asInstanceOf[Zip].value, 12345L)

    assertEquals(testBean("name"), "test")
    assertEquals(testBean("age"), 42)
    assertEquals(testBean("description"), Some("desc"))
    assertEquals(testBean("zip").asInstanceOf[Zip].value, 12345L)
  }

  test("get value of members of java class") {
    val bean: TestJavaBean = new TestJavaBean()
    val testBean = DataAdapter[TestJavaBean](bean)
    assertEquals(testBean.value("name"), "test")
    assertEquals(testBean.value("age"), 42)

  }

  test("evaluate expressions") {

    // #DataAdapterExpression
    val testBean = DataAdapter[TestBean](TestBean())
    assertEquals(testBean.value("result ${2*4}"), "result 8")
    assertEquals(testBean.value("${_self.description().get()}"), "desc")
    assertEquals(testBean.value("zip.value"), 12345)
    assertEquals(testBean.value("${_self.age() / 2}"), 21.0)
    assertEquals(testBean.value("${_self.multiply(2,3)}"), 6)
    // #DataAdapterExpression
    assertEquals(testBean.value("doubleAge()"), 84)
  }

  test("update expressions") {
    val testBean = DataAdapter[TestBean](TestBean())
    testBean.updateValue("age", 40)
  }

  test("update child expressions") {
    val testBean = DataAdapter[ParentBean](ParentBean())
    testBean.updateValue("parentName", "parent")
    testBean.updateValue("child.childName", "child")

  }

  test("get observable property") {
    val testBean = DataAdapter[TestBean](TestBean())

    assertEquals(testBean.longValue("age"), 42L)

  }

  test("handle complex case classes") {
    val book = Book(1, "Programming In Scala", 852, Author("Martin Odersky"))

    val adapter = DataAdapter[Book](book)
    assertEquals(adapter.value("title"), "Programming In Scala")
    assertEquals(adapter.value("author.name"), "Martin Odersky")
    adapter.updateValue("author.name", "M. Odersky")
    assertEquals(adapter.value("author.name"), "M. Odersky")
    assertEquals(adapter.data.author.name, "M. Odersky")
  }

  test("handle scala map ") {
    val map = Map[String, Any]("test" -> 3, "test.test" -> 4)

    val book = DataAdapter[Map[String, Any]](map)

    assertEquals(book.value("test"), 3)
    assertEquals(book.value("test.test"), 4)

  }

  test("handle changes ") {
    val testBean = DataAdapter[ParentBean](ParentBean())

    assert(!testBean.hasChanges())

    testBean.updateValue("parentName", "newName")
    assertEquals(testBean.value("parentName"), "newName")
    assert(testBean.hasChanges())

    testBean.revert()
    assertEquals(testBean.value("parentName"), "parentName")
    assert(!testBean.hasChanges())

    testBean.updateValue("child.childName", "newName")
    assertEquals(testBean.value("child.childName"), "newName")
    assert(testBean.hasChanges())
    testBean.updateValue("parentName", "newName")
    testBean.updateValue("parentName", "parentName")
    assert(testBean.hasChanges())

    testBean.revert()
    assertEquals(testBean.value("child.childName"), "childName")

    assert(!testBean.hasChanges())
  }

  test("update value with conversion") {

    val testBean = DataAdapter[TestBean](TestBean())
    testBean.updateValue("age", "2")
    assertEquals(testBean.intOption("age"), Some(2))
    testBean.updateValue("age", 3L)
    assertEquals(testBean.intOption("age"), Some(3))
    testBean.updateValue("age", 4.0)
    assertEquals(testBean.intOption("age"), Some(4))
    testBean.updateValue("age", 5.0f)
    assertEquals(testBean.intOption("age"), Some(5))

  }

  test("update values") {

    val testBean = DataAdapter[TestBean](TestBean())
    testBean.updateValues(Map("age" -> 2))
    assertEquals(testBean.intOption("age"), Some(2))

  }

}
