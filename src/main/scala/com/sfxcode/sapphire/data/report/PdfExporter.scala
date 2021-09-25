package com.sfxcode.sapphire.data.report

import better.files.File
import net.sf.jasperreports.`export`._
import net.sf.jasperreports.engine._
import net.sf.jasperreports.engine.`export`.JRPdfExporter

import java.net.URL

case class PdfExporter(jasperUrl: URL, reportContext: JasperReportsContext = DefaultJasperReportsContext.getInstance())
  extends AbstractExporter(jasperUrl) {
  private val exporter = new JRPdfExporter(reportContext)

  def exportReport(
    exportFile: File,
    parameter: Map[String, AnyRef] = Map(),
    dataSource: JRDataSource = new JREmptyDataSource(),
    exporterConfiguration: PdfExporterConfiguration = new SimplePdfExporterConfiguration(),
    reportConfiguration: SimplePdfReportConfiguration = new SimplePdfReportConfiguration()): ReportExportResult = {

    val startTime = System.currentTimeMillis()
    try {
      exporter.setExporterInput(new SimpleExporterInput(fillReport(parameter, dataSource)))
      exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(exportFile.toJava))
      exporter.setConfiguration(exporterConfiguration)
      exporter.setConfiguration(reportConfiguration)
      exporter.exportReport()
      ReportExportResult(completed = true, System.currentTimeMillis() - startTime, exportFile, jasperUrl)
    } catch {
      case e: Exception =>
        logger.debug(e.getMessage, e)
        ReportExportResult(completed = false, System.currentTimeMillis() - startTime, exportFile, jasperUrl, Some(e))
    }

  }

}
