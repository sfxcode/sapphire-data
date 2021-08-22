package com.sfxcode.sapphire.data.report

import better.files.{File, Resource}
import com.sfxcode.sapphire.data.test.PersonDatabase
import com.typesafe.scalalogging.LazyLogging

class PdfExporterSpec extends munit.FunSuite with LazyLogging {
  val friends = PersonDatabase.testFriends

  test("export simple Report") {
    val exporter     = PdfExporter(Resource.getUrl("report/TestReport.jasper"))
    val exportResult = exporter.exportReport(File.newTemporaryFile())
    println(exportResult.exportFile)
    assert(exportResult.completed)
  }

  test("export Report with Parameter") {
    val exporter     = PdfExporter(Resource.getUrl("report/test.jrxml"))
    val exportResult = exporter.exportReport(File.newTemporaryFile(), Map("test" -> "My Test"))
    println(exportResult.exportFile)
    assert(exportResult.completed)
  }

  test("export Report with DataSource") {
    val exporter   = PdfExporter(Resource.getUrl("report/Simple_Blue.jrxml"))
    val dataSource = AdapterDataSource(friends)

    val exportResult = exporter.exportReport(File.newTemporaryFile(), Map("test" -> "My Test"), dataSource)

    println(exportResult.exportFile)
    assert(exportResult.completed)
  }

}
