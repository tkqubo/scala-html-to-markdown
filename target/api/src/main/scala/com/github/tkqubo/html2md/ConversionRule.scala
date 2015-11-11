package com.github.tkqubo.html2md

import org.jsoup.nodes.Element

/**
  * Defines the html tag conversion rule
  */
case class ConversionRule(
  /**
    * Returns whether the `org.jsoup.nodes.Element` instance should be converted by this instance
    */
  shouldConvert: Matcher,
  /**
    * Converts the `org.jsoup.nodes.Element` instance into markdown text
    */
  convert: Converter
)

/**
  * Defines implicit conversions for [[ConversionRule]]
  */
object ConversionRule {
  private def byName(tagName: String): Element => Boolean = {_.tagName() == tagName}
  private def byNames(tagNames: Seq[String]): Element => Boolean = {element => tagNames.contains(element.tagName()) }

  private def constant(text: String): Converter = { (content, element) => text }

  private def fromString(converter: StringConverter): Converter = { (content, element) =>
    converter(content)
  }
  private def fromElement(converter: ElementConverter): Converter = { (content, element) =>
    converter(element)
  }

  implicit def stringAndString(tuple: (String, String)): ConversionRule =
    ConversionRule(byName(tuple._1), constant(tuple._2))
  implicit def symbolAndString(tuple: (Symbol, String)): ConversionRule =
    stringAndString((tuple._1.name, tuple._2))

  implicit def stringAndStringConverter(tuple: (String, StringConverter)): ConversionRule =
    ConversionRule(byName(tuple._1), fromString(tuple._2))
  implicit def symbolAndStringConverter(tuple: (Symbol, StringConverter)): ConversionRule =
    stringAndStringConverter((tuple._1.name, tuple._2))
  implicit def stringAndElementConverter(tuple: (String, ElementConverter)): ConversionRule =
    ConversionRule(byName(tuple._1), fromElement(tuple._2))
  implicit def symbolAndElementConverter(tuple: (Symbol, ElementConverter)): ConversionRule =
    stringAndElementConverter((tuple._1.name, tuple._2))

  implicit def stringAndConverter(tuple: (String, Converter)): ConversionRule =
    ConversionRule(byName(tuple._1), tuple._2)
  implicit def symbolAndConverter(tuple: (Symbol, Converter)): ConversionRule =
    stringAndConverter((tuple._1.name, tuple._2))

  implicit def stringsAndString(tuple: (Seq[String], String)): ConversionRule =
    ConversionRule(byNames(tuple._1), constant(tuple._2))
  implicit def symbolsAndString(tuple: (Seq[Symbol], String)): ConversionRule =
    stringsAndString((tuple._1.map(_.name), tuple._2))

  implicit def stringsAndStringConverter(tuple: (Seq[String], StringConverter)): ConversionRule =
    ConversionRule(byNames(tuple._1), fromString(tuple._2))
  implicit def symbolsAndStringConverter(tuple: (Seq[Symbol], StringConverter)): ConversionRule =
    stringsAndStringConverter((tuple._1.map(_.name), tuple._2))
  implicit def stringsAndElementConverter(tuple: (Seq[String], ElementConverter)): ConversionRule =
    ConversionRule(byNames(tuple._1), fromElement(tuple._2))
  implicit def symbolsAndElementConverter(tuple: (Seq[Symbol], ElementConverter)): ConversionRule =
    stringsAndElementConverter((tuple._1.map(_.name), tuple._2))

  implicit def stringsAndConverter(tuple: (Seq[String], Converter)): ConversionRule =
    ConversionRule(byNames(tuple._1), tuple._2)
  implicit def symbolsAndConverter(tuple: (Seq[Symbol], Converter)): ConversionRule =
    stringsAndConverter((tuple._1.map(_.name), tuple._2))

  implicit def matcherAndString(tuple: (Matcher, String)): ConversionRule =
    ConversionRule(tuple._1, constant(tuple._2))
  implicit def matcherAndStringConverter(tuple: (Matcher, StringConverter)): ConversionRule =
    ConversionRule(tuple._1, fromString(tuple._2))
  implicit def matcherAndElementConverter(tuple: (Matcher, ElementConverter)): ConversionRule =
    ConversionRule(tuple._1, fromElement(tuple._2))
  implicit def matcherAndConverter(tuple: (Matcher, Converter)): ConversionRule =
    ConversionRule(tuple._1, tuple._2)

}

