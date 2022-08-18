package com.sfxcode.sapphire.data.report

import com.sfxcode.sapphire.data.DataAdapter
import com.sfxcode.sapphire.data.report.AdapterDataSource.PatternReplacements
import net.sf.jasperreports.engine.{ JRField, JRRewindableDataSource }

case class AdapterDataSource[T <: AnyRef](dataList: List[DataAdapter[T]])
  extends JRRewindableDataSource
  with Serializable {
  private var index = -1

  override def moveFirst(): Unit = index = -1

  override def next(): Boolean = {
    index = index + 1
    dataList.nonEmpty && index < dataList.size
  }

  override def getFieldValue(jrField: JRField): AnyRef = {
    val key: String = replaceExpressionPattern(jrField.getName)
    val bean: DataAdapter[_] = dataList(index)

    bean.value(key).asInstanceOf[AnyRef]
  }

  def replaceExpressionPattern(fieldName: String): String = {
    var result = fieldName
    PatternReplacements.keys.foreach { target =>
      val replacement: String = PatternReplacements(target)
      result = result.replace(target, replacement)
    }
    result
  }

}

object AdapterDataSource {
  val PatternReplacements = Map("#" -> "${", "@" -> "}", "((" -> "(\'", "))" -> "\')")

  def fromList[T <: AnyRef](dataList: List[T]): AdapterDataSource[T] =
    new AdapterDataSource[T](dataList.map(item => DataAdapter[T](item)))

}
