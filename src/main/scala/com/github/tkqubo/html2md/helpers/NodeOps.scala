package com.github.tkqubo.html2md.helpers

import org.jsoup.nodes.{Element, Node, TextNode}

import scala.collection.JavaConversions._

object NodeOps {
  val markdownAttribute = "data-converted-markdown"

  implicit class NodeOps(node: Node) {
    def markdown(text: String): Unit =
      node.attr(markdownAttribute, text)

    def markdown: String =
      node match {
        case node: Element if node.hasAttr(markdownAttribute) =>
          node.attr(markdownAttribute)
        case node: Element =>
          node.childNodes
            .map(_.markdown)
            .reduceLeftOption(_ + _)
            .getOrElse("")
        case node: TextNode =>
          node.getWholeText
        case x => s"[###ERROR: $x ###]"
      }

    def asElement: Option[Element] = node match {
      case element: Element => Some(element)
      case _ => None
    }

    def isEmptyTag: Boolean = asElement.exists(_.tag.isEmpty)

    def nonEmptyTag: Boolean = !isEmptyTag
  }
}
