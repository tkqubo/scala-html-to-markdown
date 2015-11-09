package com.github.tkqubo.html2md.converters

import com.github.tkqubo.html2md.ConversionRule
import org.jsoup.nodes.{Node, Element}
import scala.collection.JavaConversions._


class GitHubFlavoredMarkdownConverter
  extends MarkdownConverter {

  private val highlightPattern = "^.*highlight highlight-(\\S+).*$"
  private val highlightRegex = highlightPattern.r

  val rules: Seq[ConversionRule] = Seq(
    'br -> "\n",

    Seq('del, 's, 'strike) -> { content: String => s"~~$content~~" },

    // checkbox
    { e: Element => e.attr("type") == "checkbox" && e.parent().tagName() == "li" } ->
      { e: Element => (if (e.hasAttr("checked")) "[x]" else "[ ]") + " " },

    Seq('th, 'td) -> cell _,

    'tr -> { (content: String, e: Element) =>
      if (e.parent.tagName == "thead") {
        val borderCells = e.children.map { head =>
          val align = Option(head.attr("align"))
          val marker = align match {
            case Some("left") => ":--"
            case Some("right") => "--:"
            case Some("center") => ":-:"
            case _ => "---"
          }
          cell(marker, head)
        }.mkString("")
        s"\n$content\n$borderCells"
      } else {
        s"\n$content"
      }
    },

    'table -> { content: String => s"\n\n$content\n\n" },

    Seq('thead, 'tbody, 'tfoot) -> { content: String => content },

    // fenced code blocks
    { e: Element => e.tagName == "pre" && e.children.headOption.exists(_.tagName == "code") } ->
      { e: Element => s"\n\n```\n${e.children.head.text}\n```\n\n" },

    // syntax-highlighted code blocks
    { e: Element => e.tagName == "pre" && e.parent.tagName == "div" && hasHighlight(e.parent) } -> { e: Element =>
      val language = e.parent.attr("className") match {
        case highlightRegex(found) => found
        case _ => ""
      }
      s"\n\n```$language\n${e.text}\n```\n\n"
    }, { e: Element => e.tagName == "div" && hasHighlight(e) } -> { content: String => s"\n\n$content\n\n" }
  )

  private def cell(content: String, e: Element): String = {
    val index = e.parent.children.indexOf(e)
    val prefix = if (index == 0) "|" else ""
    s"$prefix $content |"
  }

  private def hasHighlight(node: Node): Boolean =
    Option(node.attr("className")).exists(_.matches(highlightPattern))
}
