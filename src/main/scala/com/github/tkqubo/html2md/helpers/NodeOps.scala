package com.github.tkqubo.html2md.helpers

import org.jsoup.nodes.{Element, Node, TextNode}

import scala.collection.JavaConversions._

object NodeOps {
  val markdownAttribute = "data-converted-markdown"

  implicit class NodeOps(node: Node) {
    def markdownText: String = {
      val texts: Seq[String] = node.childNodes().map {
        case node: Element if node.hasAttr(markdownAttribute) =>
          node.attr(markdownAttribute)
        case node: Element =>
          node.html()
        case node: TextNode =>
          node.text()
        case x => s"[###ERROR: $x ###]"
      }

      texts.reduceLeftOption(_ + _).getOrElse("")
    }

    def asElement: Option[Element] = node match {
      case element: Element => Some(element)
      case _ => None
    }

    def isEmptyTag: Boolean = asElement.exists(_.tag.isEmpty)

    def nonEmptyTag: Boolean = !isEmptyTag
  }
}
