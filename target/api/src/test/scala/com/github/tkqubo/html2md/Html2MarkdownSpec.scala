package com.github.tkqubo.html2md

import org.specs2.mutable.Specification

/**
  * Test class for [[Html2Markdown]]
  */
class Html2MarkdownSpec
  extends Specification {
  import Html2Markdown._
  "Html2Markdown.toMarkdown" should {
    "render plain text" in {
      toMarkdown("foo") === "foo"
    }
    "render <ol>-like expression" in {
      toMarkdown("1. foo") === "1\\. foo"
    }
    "render <p>" in {
      toMarkdown("<p>paragraph</p>") === "paragraph"
    }
    "render <br>" in {
      toMarkdown("a<br>b") === "a  \nb"
    }
    "render <h1>~<h6>" in {
      toMarkdown("<h1>Header 1</h1>") === "# Header 1"
      toMarkdown("<h2>Header 2</h2>") === "## Header 2"
      toMarkdown("<h3>Header 3</h3>") === "### Header 3"
      toMarkdown("<h4>Header 4</h4>") === "#### Header 4"
      toMarkdown("<h5>Header 5</h5>") === "##### Header 5"
      toMarkdown("<h6>Header 6</h6>") === "###### Header 6"
    }
    "render <hr>" in {
      toMarkdown("<hr>") === "* * *"
      toMarkdown("<hr></hr>") === "* * *"
    }
    "render <em> and <i>" in {
      toMarkdown("<em>emphasized</em>") === "_emphasized_"
      toMarkdown("<i>italic</i>") === "_italic_"
    }
    "render <strong> and <b>" in {
      toMarkdown("<strong>strong text</strong>") === "**strong text**"
      toMarkdown("<b>bold</b>") === "**bold**"
    }
    "render <code> which is not a child of <pre>" in {
      toMarkdown("<code>public static void main</code>") === "`public static void main`"
    }
    "render <a>" in {
      "<a href=...>" in {
        toMarkdown("""<a href="https://www.google.com">Google</a>""") === "[Google](https://www.google.com)"
      }
      "<a href=... title=...>" in {
        toMarkdown("""<a href="https://www.google.com" title="Top page">Google</a>""") ===
          """[Google](https://www.google.com "Top page")"""
      }
    }
    "render <img>" in {
      "without src attr" in {
        toMarkdown("<img alt=3>") === ""
      }
      "<img src=...>" in {
        toMarkdown("""<img src="image.gif">""") === "![](image.gif)"
      }
      "<img src=... alt=...>" in {
        toMarkdown("""<img src="image.gif" alt="sample">""") === "![sample](image.gif)"
      }
      "<img src=... title=...>" in {
        toMarkdown("""<img src="image.gif" title="sample">""") === """![](image.gif "sample")"""
      }
    }
    "render <pre><code>...</pre></code>" in {
      toMarkdown(
        """<pre><code>
          |class Html2Markdown {
          |  val msg = "yo"
          |}
          |</code></pre>""".stripMargin) ===
        """    class Html2Markdown {
          |      val msg = "yo"
          |    }""".stripMargin
    }
    "render <blockquote>" in {
      toMarkdown(
        """<blockquote>
          |line 1
          |
          |line 2
          |<b>line 3</b>
          |
          |
          |line 4
          |</blockquote>""".stripMargin) ===
        """> line 1
          |>
          |> line 2
          |> **line 3**
          |>
          |> line 4"""
          .stripMargin
          .replaceAll("(?m)>$", "> ")
    }
    "render <li>" in {
      toMarkdown("<li>foo</li>") === "*   foo"
    }
    "render <ol>" in {
      toMarkdown(
        """
          |<ol>
          |  <li>list 1</li>
          |  <li>list 2
          |</ol>
          |""".stripMargin) ===
            """1.  list 1
              |2.  list 2""".stripMargin
    }
    "render <ul>" in {
      toMarkdown(
        """
          |<ul>
          |  <li>list 1</li>
          |  <li>list 2
          |  <li>list 3
          |</ul>
          |""".stripMargin) ===
            """*   list 1
              |*   list 2
              |
              |*   list 3""".stripMargin
    }
    "render <ul> in <ol>" in {
      toMarkdown(
        """
          |<ol>
          |  <li>
          |    nested
          |    <ul>
          |      <li>list 1</li>
          |      <li>list 2
          |      <li>list 3
          |    </ul>
          |  </li>
          |</ol>
          |""".stripMargin) ===
        """1.  nested
          |
          |    *   list 1
          |    *   list 2
          |
          |    *   list 3""".stripMargin
    }
    "render <div>" in {
      toMarkdown("""<div id="d1" class="division">content</div>""") ===
        """<div id="d1" class="division">
          | content
          |</div>""".stripMargin
    }
    "render <span>" in {
      toMarkdown("""<span id="i1" class="main">content</span>""") ===
        """<span id="i1" class="main">content</span>"""
    }
  }
}

