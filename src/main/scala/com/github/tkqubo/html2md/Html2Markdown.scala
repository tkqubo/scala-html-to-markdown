package com.github.tkqubo.html2md

import com.github.tkqubo.html2md.converters.MarkdownConverter
import com.github.tkqubo.html2md.helpers.NodeOps._
import org.jsoup.Jsoup
import org.jsoup.nodes._

import scala.collection.JavaConverters._
import scala.collection.mutable.ListBuffer

/**
  * Converts html text into markdown text
  * @param converter
  */
class Html2Markdown(val converter: MarkdownConverter) {
  /**
    * Returns markdown text converted from the given `html` string
    * @param html
    */
  def toMarkdown(html: String): String = {
    // Escape potential ol triggers
    val mdEscapedHtml = """(\d+)\. """.r.replaceAllIn(html, """$1\\. """)

    val document: Document = Jsoup.parse(mdEscapedHtml)
//    document.outputSettings.prettyPrint(false)
    val body: Element = document.body
    val elements: Seq[Node] = flatten(body).reverse
    elements.foreach(converter.provideMarkdown)

    body
      .markdown
      .replaceAll("(?s)^[\t\r\n]+|[\t\r\n\\s]+$", "")
      .replaceAll("(?m)\n\\s+\n", "\n\n")
      .replaceAll("(?m)\n{3,}", "\n\n")
  }

  private def flatten(node: Node): Seq[Node] = {
    //TODO: could be betteer
    var inQueue = new ListBuffer[Node] += node
    var outQueue = new ListBuffer[Node]
    while (inQueue.nonEmpty) {
      var e: Node = inQueue.head
      inQueue = inQueue.tail
      outQueue += e
      inQueue ++= e.childNodes().asScala
    }
    outQueue.tail.toSeq
  }
}

/** Companion object for [[Html2Markdown]] */
object Html2Markdown {
  /**
    * Returns markdown text converted from the given [html] string.
    * By specifying the [converter] object, you can customize conversion strategy
    * @param html
    * @param converter
    */
  def toMarkdown(html: String, converter: MarkdownConverter = MarkdownConverter.Default): String =
    new Html2Markdown(converter).toMarkdown(html)
  
  /**
    * Returns markdown text converted from the given [html] string with [[MarkdownConverter.Default]] converter.
    * @param html
    */
  def toMarkdown(html: String): String =
    toMarkdown(html, MarkdownConverter.Default)
}
