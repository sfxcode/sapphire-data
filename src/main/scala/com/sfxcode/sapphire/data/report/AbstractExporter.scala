package com.sfxcode.sapphire.data.report

import java.net.URL
import java.util

import better.files.File
import com.sfxcode.sapphire.data.report.AbstractExporter._
import com.typesafe.scalalogging.LazyLogging
import net.sf.jasperreports.engine._
import net.sf.jasperreports.engine.util.JRLoader

case class ReportExportResult(
    completed: Boolean,
    executionTime: Long,
    exportFile: File,
    jasperUrl: URL,
    exception: Option[Exception] = None
)

abstract class AbstractExporter(jasperUrl: URL) extends LazyLogging {

  protected def fillReport(parameter: Map[String, AnyRef], dataSource: JRDataSource): JasperPrint = {
    val jasperReport = {

      val file = File(jasperUrl)

      if (file.exists && file.pathAsString.endsWith(SuffixJrxml)) {
        val jasperFile = File.newTemporaryFile(suffix = SuffixJasper)
        JasperCompileManager.compileReportToFile(file.pathAsString, jasperFile.pathAsString)
        JRLoader.loadObject(jasperFile.url).asInstanceOf[JasperReport]
      }
      else
        JRLoader.loadObject(jasperUrl).asInstanceOf[JasperReport]

    }
    val parameterMap = new util.HashMap[String, AnyRef]()
    parameter.keys.foreach(key => parameterMap.put(key, parameter(key)))

    JasperFillManager.fillReport(jasperReport, parameterMap, dataSource)
  }

}

object AbstractExporter {
  val SuffixJrxml  = ".jrxml"
  val SuffixJasper = ".jasper"
}
