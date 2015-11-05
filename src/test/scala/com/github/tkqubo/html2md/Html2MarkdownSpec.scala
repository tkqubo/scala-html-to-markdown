package com.github.tkqubo.html2md

import org.specs2.mutable.Specification


class Html2MarkdownSpec
  extends Specification {
  import Html2Markdown._
  "Html2Markdown.toMarkdown" should {
    "render plain text" in {
      toMarkdown("foo") === "foo"
    }
    "render ol-like expression" in {
      toMarkdown("1. foo") === "1\\. foo"
    }
    "render p" in {
      toMarkdown("<p>paragraph</p>") === "\n\nparagraph\n\n"
    }
    "render br" in {
      toMarkdown("a<br>b") === "a  \nb"
    }
    "render h1~h6" in {
      toMarkdown("<h1>Header 1</h1>") === "\n\n# Header 1\n\n"
      toMarkdown("<h2>Header 2</h2>") === "\n\n## Header 2\n\n"
      toMarkdown("<h3>Header 3</h3>") === "\n\n### Header 3\n\n"
      toMarkdown("<h4>Header 4</h4>") === "\n\n#### Header 4\n\n"
      toMarkdown("<h5>Header 5</h5>") === "\n\n##### Header 5\n\n"
      toMarkdown("<h6>Header 6</h6>") === "\n\n###### Header 6\n\n"
    }
    "render hr" in {
      toMarkdown("<hr>") === "\n\n* * *\n\n"
    }
    "render em and i" in {
      toMarkdown("<em>emphasized</em>") === "_emphasized_"
      toMarkdown("<i>italic</i>") === "_italic_"
    }
    "render strong and b" in {
      toMarkdown("<strong>strong text</strong>") === "**strong text**"
      toMarkdown("<b>bold</b>") === "**bold**"
    }
  }
}

