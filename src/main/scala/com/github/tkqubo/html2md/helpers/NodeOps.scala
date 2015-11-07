package com.github.tkqubo.html2md.helpers

import org.jsoup.nodes.{Element, Node, TextNode}

import scala.collection.JavaConversions._

object NodeOps {
  /** Node's attribute name that hold a markdown text */
  val markdownAttribute = "data-converted-markdown"

  /** Provides extended method for [[Node]] */
  implicit class NodeOps(node: Node) {
    /**
      * Sets markdown text for this node
      * @param text markdown text
      */
    //noinspection ScalaStyle
    def markdown_=(text: String): Unit =
      node.attr(markdownAttribute, text)

    /** Returns markdown of this node */
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

    /** Returns this node casted as [[Element]], wrapped in [[Option]].
      * If this node is not an instance of [[Element]], it returns [[None]]
      */
    def asElement: Option[Element] = node match {
      case element: Element => Some(element)
      case _ => None
    }

    /** Returns whether this node is an instance of an empty tag (e.g. {@code <br>}, {@code <hr>}) */
    def isEmptyTag: Boolean = asElement.exists(_.tag.isEmpty)

    /** Returns whether this node is not an instance of an empty tag (e.g. {@code <br>}, {@code <hr>}) */
    def nonEmptyTag: Boolean = !isEmptyTag
  }
}
