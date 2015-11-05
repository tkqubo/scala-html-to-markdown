package com.github.tkqubo.html2md

import org.jsoup.nodes.Element

class ConversionRule(converter: ((String, Element) => String), matcher: Element => Boolean) {
  def shouldConvert(element: Element): Boolean = matcher(element)
  def convert(content: String, element: Element): String = converter(content, element)
}

object ConversionRule {
  def tag(tagName: String): RuleBuilder =
    new RuleBuilder({ _.tagName == tagName })
  def tags(tagNames: String*): RuleBuilder =
    new RuleBuilder({ element => tagNames.contains(element.tagName) })
  def matchingTags(matcher: Element => Boolean): RuleBuilder =
    new RuleBuilder(matcher)
  def rule(matcher: Element => Boolean, converter: (String, Element) => String): ConversionRule =
    new ConversionRule(converter, matcher)

  class RuleBuilder private[ConversionRule] (matcher: Element => Boolean) {
    def render(text: String): ConversionRule =
      new ConversionRule({ (content: String, element: Element) => text }, matcher)

    def render(processContent: String => String): ConversionRule =
      new ConversionRule({ (content: String, element: Element) => processContent(content) }, matcher)

    def render(converter: (String, Element) => String): ConversionRule =
      new ConversionRule(converter, matcher)
  }

  implicit def toRule(tagName: Symbol): RuleBuilder = tag(tagName.name)
  implicit def toRule(tagNames: Seq[Symbol]): RuleBuilder = tags(tagNames.map(_.name):_*)
}

