package com.github.tkqubo.html2md.converters

import org.jsoup.nodes.Element
import scala.collection.JavaConversions._

class GitHubFlavoredMarkdownConverter extends {
  private val cell: (String, Element) => String =
    GitHubFlavoredMarkdownConverter.TableHelper.cell
} with MarkdownConverter(Seq(
  'br -> "\n",

  Seq('del, 's, 'strike) -> { content: String => s"~~$content~~" },

  // checkbox
  { e: Element => e.attr("type") == "checkbox" && e.parent().tagName() == "li" } ->
    { e: Element => (if (e.hasAttr("checked")) "[x]" else "[ ]") + " " },

  Seq('th, 'td) -> cell,

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

  Seq('thead, 'tbody, 'tfoot) -> { content: String => content }
))

object GitHubFlavoredMarkdownConverter {
  private object TableHelper {
    def cell(content: String, e: Element): String = {
      val index = e.parent.children.indexOf(e)
      val prefix = if (index == 0) "|" else ""
      s"$prefix $content |"
    }
  }
}

