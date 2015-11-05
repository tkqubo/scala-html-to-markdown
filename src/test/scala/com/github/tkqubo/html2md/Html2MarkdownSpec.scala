package com.github.tkqubo.html2md

import org.specs2.mutable.Specification


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
      toMarkdown("<p>paragraph</p>") === "\n\nparagraph\n\n"
    }
    "render <br>" in {
      toMarkdown("a<br>b") === "a  \nb"
    }
    "render <h1>~<h6>" in {
      toMarkdown("<h1>Header 1</h1>") === "\n\n# Header 1\n\n"
      toMarkdown("<h2>Header 2</h2>") === "\n\n## Header 2\n\n"
      toMarkdown("<h3>Header 3</h3>") === "\n\n### Header 3\n\n"
      toMarkdown("<h4>Header 4</h4>") === "\n\n#### Header 4\n\n"
      toMarkdown("<h5>Header 5</h5>") === "\n\n##### Header 5\n\n"
      toMarkdown("<h6>Header 6</h6>") === "\n\n###### Header 6\n\n"
    }
    "render <hr>" in {
      toMarkdown("<hr>") === "\n\n* * *\n\n"
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
          |</code></pre>""".stripMargin) === "\n\n    class Html2Markdown {\n      val msg = \"yo\"\n    }\n\n"
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
        "\n\n> line 1\n> \n> line 2\n> **line 3**\n> \n> line 4\n\n"
    }
    "render <li>" in {
      toMarkdown("<li>foo</li>") === "*  foo"
    }
    "render <ol>" in {
      toMarkdown(
        """
          |<ol>
          |  <li>list 1</li>
          |  <li>list 2
          |</ol>
          |""".stripMargin) === "\n\n1.  list 1\n2.  list 2\n\n\n"
    }
    "render <ul>" in {
      toMarkdown(
        """
          |<ul>
          |  <li>list 1</li>
          |  <li>list 2
          |  <li>list 3
          |</ul>
          |""".stripMargin) === "\n\n*  list 1\n*  list 2\n*  list 3\n\n\n"
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
        "\n\n1.  nested\n        \n    \n    *  list 1\n    *  list 2\n    *  list 3\n\n\n"
    }
  }
}

