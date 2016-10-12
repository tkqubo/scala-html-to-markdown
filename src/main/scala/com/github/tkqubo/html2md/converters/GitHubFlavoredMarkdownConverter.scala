package com.github.tkqubo.html2md.converters

import com.github.tkqubo.html2md.ConversionRule
import org.jsoup.nodes.{Node, Element}
import scala.collection.JavaConversions._


/**
  * Converts html text into GitHub flavored markdown text
  * see. [GitHub Flavored Markdown](https://help.github.com/articles/github-flavored-markdown/)
  */
class GitHubFlavoredMarkdownConverter
  extends DefaultMarkdownConverter {
  override protected def rules: Seq[ConversionRule] = Seq[ConversionRule](
    'br -> "\n",

    Seq('del, 's, 'strike) -> { content: String => s"~~$content~~" },

    // checkbox
    { e: Element => e.attr("type") == "checkbox" && e.parent().tagName() == "li" } ->
      { e: Element => (if (e.hasAttr("checked")) "[x]" else "[ ]") + " " },

    Seq('th, 'td) -> cell _,

    'tr -> { (content: String, e: Element) =>
      val trimmedContent = content
        .split("\n")
        .map(_.trim + " ")
        .mkString("").trim
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
        s"$trimmedContent\n${borderCells.trim}"
      } else {
        trimmedContent
      }
    },

    'table -> { content: String =>
      val trimmedContent = content
        .split("\n")
        .map(_.replaceAll("^[ \r\n\t]+|[ \r\n\t]+$", ""))
        .filter(_.nonEmpty)
        .mkString("\n")
      s"\n\n$trimmedContent\n\n"
    },

    Seq('thead, 'tbody, 'tfoot) -> { content: String => content.trim },

    // fenced code blocks
    { e: Element => e.tagName == "pre" && e.children.headOption.exists(_.tagName == "code") } ->
      { e: Element => s"\n\n```\n${e.children.head.text}```\n\n" },

    // syntax-highlighted code blocks
    { e: Element => e.tagName == "pre" && e.parent.tagName == "div" && hasHighlight(e.parent) } -> { e: Element =>
      val language = e.parent.attr("class") match {
        case highlightRegex(found) => found
        case _ => ""
      }
      s"\n\n```$language\n${e.text}```\n\n"
    },

    { e: Element => e.tagName == "div" && hasHighlight(e) } -> { content: String => s"\n\n$content\n\n" }
  ) ++ super.rules

  private val highlightPattern = "^.*highlight highlight-(\\S+).*$"
  private val highlightRegex = highlightPattern.r

  private def cell(content: String, e: Element): String = {
    val index = e.parent.children.indexOf(e)
    val prefix = if (index == 0) "|" else ""
    s"$prefix $content |"
  }

  private def hasHighlight(node: Node): Boolean =
    Option(node.attr("class")).exists(_.matches(highlightPattern))
}
