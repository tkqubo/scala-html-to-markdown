package com.github.tkqubo.html2md.converters

import com.github.tkqubo.html2md.ConversionRule
import com.github.tkqubo.html2md.helpers.NodeOps._
import org.jsoup.nodes._

/**
  * Performs html tag conversion according to the given [rules]
  */
trait MarkdownConverter {
  def rules: Seq[ConversionRule]
  /**
    * Provide the `org.jsoup.nodes.Node` instance with its markdown representation.
    *
    * The markdown text will be stored in `"data-converted-markdown"` attribute
    * @param node
    */
  def provideMarkdown(node: Node): Unit =
    node.markdown = convert(node)

  /**
    * Converts the `org.jsoup.nodes.Node` instance into its markdown representation
    * @param node
    * @return
    */
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

  /**
    * Returns a new [[MarkdownConverter]] instance whose conversion rules are the concatenation of this instance and the given [[MarkdownConverter]]
    * @param that
    * @return
    */
  //noinspection ScalaStyle
  def ++(that: MarkdownConverter): MarkdownConverter =
    MarkdownConverter(this.rules ++ that.rules)

  /**
    * Returns a new [[MarkdownConverter]] instance whose conversion rules is the concatenation of this instance and the given [[ConversionRule]]
    * @param rule
    * @return
    */
  //noinspection ScalaStyle
  def +(rule: ConversionRule): MarkdownConverter =
    MarkdownConverter(this.rules :+ rule)

  /**
    * Returns a new [[MarkdownConverter]] instance whose conversion rules is the concatenation of the given [[ConversionRule]] and this instance
    * @param rule
    * @return
    */
  //noinspection ScalaStyle
  def +:(rule: ConversionRule): MarkdownConverter =
    MarkdownConverter(rule +: this.rules)

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

/**
  * Defines available list of [[MarkdownConverter]]s
  */
object MarkdownConverter {
  def apply(_rules: Seq[ConversionRule]): MarkdownConverter = new MarkdownConverter {
    override val rules: Seq[ConversionRule] = _rules
  }

  val Default: MarkdownConverter = new DefaultMarkdownConverter
  var Gfm: MarkdownConverter = new GitHubFlavoredMarkdownConverter
}
