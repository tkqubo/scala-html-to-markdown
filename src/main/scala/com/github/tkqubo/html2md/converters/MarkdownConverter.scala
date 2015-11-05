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

  val Default = createInstance(
    'p -> { content: String => s"\n\n$content\n\n" },

    'br -> "  \n",

    Seq('h1, 'h2, 'h3, 'h4, 'h5, 'h6) -> { (content: String, element: Element) =>
      val level = element.tagName().charAt(1).toString.toInt
      val prefix = 1.to(level).map(_ => "#").reduce(_ + _)
      s"\n\n$prefix $content\n\n"
    },

    'hr -> "\n\n* * *\n\n",

    Seq('em, 'i) -> { (content: String) => s"_${content}_" },

    Seq('strong, 'b) -> { (content: String) => s"**$content**" },

    // inline code
    { e: Element =>
      //noinspection ScalaStyle
      val hasSiblings = e.nextSibling != null || e.previousSibling != null
      val isCodeBlock = e.parent.tagName == "pre" && !hasSiblings
      e.tagName == "code" && !isCodeBlock
    } -> { (code: String) => s"`$code`" },

    // <a> with href attr
    { e: Element =>
      e.tagName() == "a" && e.hasAttr("href")
    } -> { (text: String, e: Element) =>
      val titlePart = if (e.hasAttr("title")) s""" "${e.attr("title")}"""" else ""
      s"""[$text](${e.attr("href")}$titlePart)"""
    },

    'img -> { (e: Element) =>
      val titlePart = if (e.hasAttr("title")) s""" "${e.attr("title")}"""" else ""
      if (e.hasAttr("src")) {
        s"""![${e.attr("alt")}](${e.attr("src")}$titlePart)"""
      } else {
        ""
      }
    }
  )
}
