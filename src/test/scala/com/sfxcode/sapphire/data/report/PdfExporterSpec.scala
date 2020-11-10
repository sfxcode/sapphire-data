package com.sfxcode.sapphire.data.report

import better.files.{File, Resource}
import com.sfxcode.sapphire.data.test.PersonDatabase
import com.typesafe.scalalogging.LazyLogging
import org.specs2.mutable.Specification

class PdfExporterSpec extends Specification with LazyLogging {
  sequential

  "PDFReport" should {

    "export simple Report" in {
      val exporter     = PdfExporter(Resource.getUrl("report/TestReport.jasper"))
      val exportResult = exporter.exportReport(File.newTemporaryFile())
      println(exportResult.exportFile)
      exportResult.completed must beTrue
    }

    "export Report with Parameter" in {
      val exporter     = PdfExporter(Resource.getUrl("report/test.jrxml"))
      val exportResult = exporter.exportReport(File.newTemporaryFile(), Map("test" -> "My Test"))
      println(exportResult.exportFile)
      exportResult.completed must beTrue
    }

    "export Report with DataSource" in {
      val exporter   = PdfExporter(Resource.getUrl("report/Simple_Blue.jrxml"))
      val dataSource = AdapterDataSource(PersonDatabase.testFriends)

      val exportResult = exporter.exportReport(File.newTemporaryFile(), Map("test" -> "My Test"), dataSource)

      println(exportResult.exportFile)
      exportResult.completed must beTrue
    }

  }

}
