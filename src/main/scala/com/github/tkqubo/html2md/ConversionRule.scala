package com.github.tkqubo.html2md

import org.jsoup.nodes.Element

class ConversionRule(converter: ((String, Element) => String), matcher: Element => Boolean) {
  def shouldConvert(element: Element): Boolean = matcher(element)
  def convert(content: String, element: Element): String = converter(content, element)
}

object ConversionRule {
  implicit def toRule(tuple: (String, String)): ConversionRule =
    new ConversionRule(converter = { (content, element) => tuple._2}, matcher = {_.tagName() == tuple._1})
  implicit def toRule(tuple: (Symbol, String)): ConversionRule = toRule((tuple._1.name, tuple._2))

  implicit def toRule(tuple: (String, String => String)): ConversionRule =
    new ConversionRule(converter = { (content, element) => tuple._2(content) }, matcher = {_.tagName() == tuple._1})
  implicit def toRule(tuple: (Symbol, String => String)): ConversionRule = toRule((tuple._1.name, tuple._2))

  implicit def toRule(tuple: (String, (String, Element) => String)): ConversionRule =
    new ConversionRule(converter = tuple._2, matcher = {_.tagName() == tuple._1})
  implicit def toRule(tuple: (Symbol, (String, Element) => String)): ConversionRule = toRule((tuple._1.name, tuple._2))
}

