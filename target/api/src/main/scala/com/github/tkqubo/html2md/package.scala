package com.github.tkqubo

import org.jsoup.nodes.Element

/** Main package for HTML-to-Markdown */
package object html2md {
  /**
    * Type alias for the function that takes html content and html element itself as parameters and returns markdown text
    */
  type Converter = (String, Element) => String
  /**
    * Type alias for the function that takes html content as a parameter and returns markdown text
    */
  type StringConverter = String => String
  /**
    * Type alias for the function that takes html element itself a parameter and returns markdown text
    */
  type ElementConverter = Element => String
  /**
    * Type alias for the function to return whethere the element should be converted
    */
  type Matcher = (Element) => Boolean
}
