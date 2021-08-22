package com.sfxcode.sapphire.data

import com.sfxcode.sapphire.data.test.TestJavaBean
import com.typesafe.scalalogging.LazyLogging
import javafx.beans.property._

case class Author(var name: String)

case class Book(id: Long, title: String, pages: Int, author: Author)

case class Zip(value: Long = 12345)

case class TestBean(
    name: String = "test",
    age: Int = 42,
    zip: Zip = Zip(),
    description: Option[String] = Some("desc"),
    observable: Property[_] = new SimpleStringProperty("observable")
) {
  def doubleAge(): Int = age * 2

  def multiply(first: java.lang.Long, second: java.lang.Long): Long = first * second

}

class TestClass(
    var name: String = "test",
    var age: Int = 42,
    var zip: Zip = Zip(),
    var description: Option[String] = Some("desc"),
    var observable: Property[_] = new SimpleStringProperty("observable")
) {
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
    assertEquals(testBean.getValue("name"), "test")
    assertEquals(testBean.getValue("age"), 42)
    assertEquals(testBean.getValue("description"), Some("desc"))
    assertEquals(testBean.getValue("observable").asInstanceOf[StringProperty].getValue, "observable")
    assertEquals(testBean.getValue("zip").asInstanceOf[Zip].value, 12345L)

    assertEquals(testBean("name"), "test")
    assertEquals(testBean("age"), 42)
    assertEquals(testBean("description"), Some("desc"))
    assertEquals(testBean("observable").asInstanceOf[StringProperty].getValue, "observable")
    assertEquals(testBean("zip").asInstanceOf[Zip].value, 12345L)

    val testBean2 = DataAdapter[TestBean](TestBean())

    testBean2.updateValue("description", None)
    assertEquals(testBean2.wrappedData.description, None)
    assertEquals(testBean2.getValue("description"), None)

  }

  test("get value of members of class") {
    val testBean = DataAdapter[TestClass](new TestClass())
    assertEquals(testBean.getValue("name"), "test")
    assertEquals(testBean.getValue("age"), 42)
    assertEquals(testBean.getValue("description"), Some("desc"))
    assertEquals(testBean.getValue("observable").asInstanceOf[StringProperty].getValue, "observable")
    assertEquals(testBean.getValue("zip").asInstanceOf[Zip].value, 12345L)

    assertEquals(testBean("name"), "test")
    assertEquals(testBean("age"), 42)
    assertEquals(testBean("description"), Some("desc"))
    assertEquals(testBean("observable").asInstanceOf[StringProperty].getValue, "observable")
    assertEquals(testBean("zip").asInstanceOf[Zip].value, 12345L)
  }

  test("get value of members of java class") {
    val bean: TestJavaBean = new TestJavaBean()
    val testBean           = DataAdapter[TestJavaBean](bean)
    logger.debug(testBean.getProperty("date").toString)
    assertEquals(testBean.getValue("name"), "test")
    assertEquals(testBean.getValue("age"), 42)

  }

  test("evaluate expressions") {

    // #DataAdapterExpression
    val testBean = DataAdapter[TestBean](TestBean())
    assertEquals(testBean.getValue("result ${2*4}"), "result 8")
    assertEquals(testBean.getValue("${_self.description().get()}"), "desc")
    assertEquals(testBean.getValue("!{_self.description().get()}"), "desc")
    assertEquals(testBean.getValue("zip.value"), 12345)
    assertEquals(testBean.getValue("${_self.age() / 2}"), 21.0)
    assertEquals(testBean.getValue("${_self.multiply(2,3)}"), 6)
    assertEquals(testBean.getValue("!{_self.multiply(2,3)}"), 6)
    // #DataAdapterExpression

    assertEquals(testBean.getValue("doubleAge()"), 84)
  }

  test("update expressions") {
    val testBean      = DataAdapter[TestBean](TestBean())
    val observableAge = testBean.getIntegerProperty("${_self.age()}")
    val observable    = testBean.getIntegerProperty("${_self.doubleAge()}")
    assertEquals(observableAge.getValue.toInt, 42)
    assertEquals(observable.getValue.toInt, 84)

    testBean.updateValue("age", 40)
    // only member expressions updated by default
    assertEquals(observableAge.getValue.toInt, 40)
    assertEquals(observable.getValue.toInt, 80) //

  }

  test("update child expressions") {
    val testBean       = DataAdapter[ParentBean](ParentBean())
    val observableName = testBean.getStringProperty("${_self.fullName()}")
    assertEquals(observableName.getValue, "parentName : [child] childName")
    testBean.updateValue("parentName", "parent")
    assertEquals(observableName.getValue, "parent : [child] childName")
    testBean.updateValue("child.childName", "child")
    assertEquals(observableName.getValue, "parent : [child] child")

  }

  test("get observable property") {
    val testBean = DataAdapter[TestBean](TestBean())

    val observable = testBean.getIntegerProperty("age")
    assertEquals(observable.getValue.toInt, 42)

  }

  test("get observable expression property") {
    val testBean = DataAdapter[ListClass](ListClass())

    val observable = testBean.getIntegerProperty("${_self.list().size()}")
    assertEquals(observable.getValue.toInt, 2)

    val observableString = testBean.getStringProperty("List count: ${_self.list().size()}")
    assertEquals(observableString.getValue, "List count: 2")

  }

  test("handle complex case classes") {
    val book = Book(1, "Programming In Scala", 852, Author("Martin Odersky"))

    val wrapper = DataAdapter[Book](book)
    assertEquals(wrapper.getValue("title"), "Programming In Scala")
    assertEquals(wrapper.getValue("author.name"), "Martin Odersky")
    wrapper.updateValue("author.name", "M. Odersky")
    assertEquals(wrapper.getValue("author.name"), "M. Odersky")

    val observable = wrapper.getStringProperty("author.name")
    assertEquals(observable.getValue, "M. Odersky")

    observable.set("Martin Odersky")
    assertEquals(wrapper.getValue("author.name"), "Martin Odersky")
  }

  test("handle scala map ") {
    val map = Map[String, Any]("test" -> 3, "test.test" -> 4)

    val book = DataAdapter[Map[String, Any]](map)

    assertEquals(book.getValue("test"), 3)
    assertEquals(book.getValue("test.test"), 4)

  }

  test("handle changes) ") {
    val testBean = DataAdapter[ParentBean](ParentBean())

    assert(!testBean.hasChanges)

    testBean.updateValue("parentName", "newName")
    assertEquals(testBean.getValue("parentName"), "newName")
    assert(testBean.hasChanges)

    testBean.revert()
    assertEquals(testBean.getValue("parentName"), "parentName")
    assert(!testBean.hasChanges)

    testBean.updateValue("child.childName", "newName")
    assertEquals(testBean.getValue("child.childName"), "newName")
    assert(testBean.hasChanges)
    testBean.updateValue("parentName", "newName")
    testBean.updateValue("parentName", "parentName")
    assert(testBean.hasChanges)

    testBean.revert()
    assertEquals(testBean.getValue("child.childName"), "childName")

    assert(!testBean.hasChanges)
  }

  test("update value with conversion") {

    val testBean = DataAdapter[TestBean](TestBean())
    testBean.updateValue("age", "2")
    assertEquals(testBean.getIntValue("age"), Some(2))
    testBean.updateValue("age", 3L)
    assertEquals(testBean.getIntValue("age"), Some(3))
    testBean.updateValue("age", 4.0)
    assertEquals(testBean.getIntValue("age"), Some(4))
    testBean.updateValue("age", 5.0f)
    assertEquals(testBean.getIntValue("age"), Some(5))

  }

  test("update values") {

    val testBean = DataAdapter[TestBean](TestBean())
    testBean.updateValues(Map("age" -> 2))
    assertEquals(testBean.getIntValue("age"), Some(2))

  }

}
