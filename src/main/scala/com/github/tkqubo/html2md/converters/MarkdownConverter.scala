package com.github.tkqubo.html2md.converters

import com.github.tkqubo.html2md.ConversionRule
import com.github.tkqubo.html2md.helpers.NodeOps._
import org.jsoup.nodes._

class MarkdownConverter private (rules: Seq[ConversionRule]) {
  def convert(node: Node): String = {
    var markdownText = node.markdownText
    node match {
      case element: Element =>
        rules
          .filter(_.shouldConvert(element))
          .foreach { converter =>
            markdownText = converter.convert(markdownText, element)
          }
    }
    markdownText
  }
}

object MarkdownConverter {
  private def createInstance(rules: ConversionRule*) = new MarkdownConverter(rules)

  val DefaultMarkdownConverter = createInstance(
    'p -> { content: String => s"\n\n$content\n\n" }
  )
}
