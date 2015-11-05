package com.github.tkqubo.html2md

import com.github.tkqubo.html2md.converters.MarkdownConverter
import com.github.tkqubo.html2md.helpers.NodeOps._
import org.jsoup.Jsoup
import org.jsoup.nodes._
import collection.JavaConversions._

import scala.collection.mutable.ListBuffer

object Html2Markdown {
  val markdownAttribute = "data-converted-markdown"
  def toMarkdown(html: String): String = {
    // Escape potential ol triggers
    val mdEscapedHtml = """(\d+)\. """.r.replaceAllIn(html, """\1\\. """)

    val document: Document = Jsoup.parse(mdEscapedHtml)
    val elements: Seq[Node] = flatten(document).reverse
    elements.foreach(provideMarkdownText)

    document.markdownText
  }

  private def flatten(node: Node): Seq[Node] = {
    //TODO: could be betteer
    var inQueue = new ListBuffer[Node] += node
    var outQueue = new ListBuffer[Node]
    while (inQueue.nonEmpty) {
      var e: Node = inQueue.head
      inQueue = inQueue.tail
      outQueue += e
      inQueue ++= e.childNodes()
    }
    outQueue.tail
  }

  private def provideMarkdownText(node: Node): Unit = {
    val replacement = node match {
      case _ if isBlankNode(node) =>
        ""
      case node: Element =>
        MarkdownConverter.Default.convert(node)
      case node: TextNode =>
        node.getWholeText
      case x => x.outerHtml()
    }
    node.attr(markdownAttribute, replacement)
  }

  private def isBlankNode(node: Node): Boolean = {
    node.nonEmptyTag && node.markdownText.trim.isEmpty
  }
}
