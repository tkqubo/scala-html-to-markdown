package com.github.tkqubo.html2md

import org.specs2.mutable.Specification


class Html2MarkdownSpec
  extends Specification {
  import Html2Markdown._
  "Html2Markdown.toMarkdown" should {
    "render plain text" in {
      toMarkdown("foo") === "foo"
    }
  }
}

