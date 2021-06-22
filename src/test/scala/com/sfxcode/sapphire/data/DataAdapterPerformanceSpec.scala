package com.sfxcode.sapphire.data

import com.typesafe.scalalogging.LazyLogging

class DataAdapterPerformanceSpec extends munit.FunSuite with LazyLogging {

  test("get value of members of case class") {
    val testBean = DataAdapter[TestClass](new TestClass())
    assertEquals(testBean.getValue("name"), "test")
    assertEquals(testBean.getValue("age"), 42)
    val max = 1000
    val start = System.currentTimeMillis()

    (1 to max).foreach { i =>
      testBean.getValue("name")
      testBean.getValue("age")
      testBean.updateValue("name", "test")
      testBean.updateValue("age", 3)
    }

    val time = System.currentTimeMillis() - start

    logger.debug("time : %s".format(time))

    assert(time < 1000)

  }

}
