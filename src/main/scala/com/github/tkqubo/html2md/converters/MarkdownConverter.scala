package com.github.tkqubo.html2md.converters

import com.github.tkqubo.html2md.ConversionRule
import com.github.tkqubo.html2md.helpers.NodeOps._
import org.jsoup.nodes._

import scala.collection.JavaConversions._

class MarkdownConverter private (val rules: Seq[ConversionRule]) {
  def provideMarkdown(node: Node): Unit =
    node.markdown(convert(node))

  def convert(node: Node): String =
    node match {
      case element: Element if isBlank(element) =>
        ""
      case element: Element =>
        rules
          .find(_.shouldConvert(element))
          .map(applyRule(element, _))
          .getOrElse(element.markdown)
      case textNode: TextNode =>
        textNode.getWholeText
      case x =>
        x.outerHtml()
    }

  private def isBlank(element: Element): Boolean =
    element.nonEmptyTag && element.markdown.trim.isEmpty && element.tagName != "a"

  //noinspection ScalaStyle
  def ++(that: MarkdownConverter): MarkdownConverter =
    new MarkdownConverter(this.rules ++ that.rules)

  //noinspection ScalaStyle
  def +(rule: ConversionRule): MarkdownConverter =
    new MarkdownConverter(this.rules :+ rule)

  //noinspection ScalaStyle
  def +:(rule: ConversionRule): MarkdownConverter =
    new MarkdownConverter(rule +: this.rules)

  private def applyRule(element: Element, rule: ConversionRule): String = {
    val content = element.markdown
    val (leading, trailing) = flankingWhitespace(element)
    val trimmedContent = if (leading.nonEmpty || trailing.nonEmpty) content.trim else content
    val convertedContent = rule.convert(trimmedContent, element)
    s"$leading$convertedContent$trailing"
  }

  private def flankingWhitespace(element: Element): (String, String) = {
    val hasLeading = element.html.matches("(?s)^[ \r\n\t].*")
    val hasTrailing = element.html.matches("(?s).*[ \r\n\t]$")
    val leading = if (hasLeading && !isFlankedByWhitespace(element, left = true)) " " else ""
    val trailing = if (hasTrailing && !isFlankedByWhitespace(element, left = false)) " " else ""
    (leading, trailing)
  }

  private def isFlankedByWhitespace(element: Element, left: Boolean) = {
    val (sibling: Option[Node], pattern: String) = if (left) {
      (Option(element.previousSibling), " $")
    } else {
      (Option(element.nextSibling), "^ ")
    }

    if (element.tagName == "h5" && element.html.contains("line")) {
      println("isFlankedByWhitespace([element, left])")
      println(element.outerHtml())
      println(sibling)
      println(pattern)
    }
    sibling match {
      case Some(textNode: TextNode) =>
        textNode.text.matches(pattern)
      case Some(e: Element) if !e.isBlock =>
        e.text.matches(pattern)
      case _ =>
        false
    }
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

    Seq('em, 'i) -> { content: String => s"_${content}_" },

    Seq('strong, 'b) -> { content: String => s"**$content**" },

    // inline code
    { e: Element =>
      //noinspection ScalaStyle
      val hasSiblings = e.nextSibling != null || e.previousSibling != null
      val isCodeBlock = e.parent.tagName == "pre" && !hasSiblings
      e.tagName == "code" && !isCodeBlock
    } -> { code: String => s"`$code`" },

    // <a> with href attr
    { e: Element =>
      e.tagName == "a" && e.hasAttr("href")
    } -> { (text: String, e: Element) =>
      val titlePart = if (e.hasAttr("title")) s""" "${e.attr("title")}"""" else ""
      s"""[$text](${e.attr("href")}$titlePart)"""
    },

    'img -> { e: Element =>
      val titlePart = if (e.hasAttr("title")) s""" "${e.attr("title")}"""" else ""
      if (e.hasAttr("src")) {
        s"""![${e.attr("alt")}](${e.attr("src")}$titlePart)"""
      } else {
        ""
      }
    },

    // code blocks
    { e: Element =>
      e.tagName() == "pre" && e.children().headOption.exists(_.tagName() == "code")
    } -> { e: Element => s"\n\n    ${e.children().head.text.replaceAll("\n", "\n    ")}\n\n" },

    'blockquote -> { content: String =>
      val replacement = content
        .trim
        .replaceAll("(?m)^\\s+$", "")
        .replaceAll("(?m)\n{3,}", "\n\n")
        .replaceAll("(?m)^", "> ")
      s"\n\n$replacement\n\n"
    },

    'li -> { (content: String, e: Element) =>
      val replacement = content
//        .split("\n").filter(_.nonEmpty).mkString("\n")
        .replaceAll("(?s)^\\s+|[\t ]+$", "") // trailing ws removal was added
        .replaceAll("(?m)\n", "\n    ")
      val prefix = if (e.parentNode().nodeName() == "ol") {
        val index = e.parent.children.indexOf(e) + 1
        s"$index.  "
      } else {
        "*   "
      }
      prefix + replacement
    },

    Seq('ul, 'ol) -> { (content: String, e: Element) =>
      val children = e
        .children
        .filter(_.tagName == "li")
        .map(_.markdown)
        .mkString("\n")
      if (e.parentNode.nodeName == "li") {
        s"\n$children"
      } else {
        s"\n\n$children\n\n"
      }
    },

    // block element
    { e: Element => e.isBlock } -> { (content: String, e: Element) =>
      s"\n\n${e.clone.html(content).outerHtml}\n\n"
    },

    // anything else
    { _: Element => true } -> { (content: String, e: Element) =>
      e.clone.html(content).outerHtml
    }
  )
}
