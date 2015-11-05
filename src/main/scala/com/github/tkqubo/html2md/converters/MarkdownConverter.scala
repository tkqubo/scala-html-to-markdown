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
    'p -> { content: String => s"\n\n$content\n\n" },
    'br -> "  \n",
    Seq('h1, 'h2, 'h3, 'h4, 'h5, 'h6) -> { (content: String, element: Element) =>
      val level = element.tagName().charAt(1).toString.toInt
      val prefix = 1.to(level).map(_ => "#").reduce(_ + _)
      s"\n\n$prefix $content\n\n"
    }
  )
}
