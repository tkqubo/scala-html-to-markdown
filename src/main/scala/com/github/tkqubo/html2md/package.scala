package com.github.tkqubo

import org.jsoup.nodes.Element

//noinspection ScalaStyle
package object html2md {
  type Converter = (String, Element) => String
  type StringConverter = String => String
  type ElementConverter = Element => String
  type Matcher = (Element) => Boolean
}
