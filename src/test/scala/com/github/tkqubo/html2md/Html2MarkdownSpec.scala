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

    // tests below are taken from https://github.com/domchristie/to-markdown/blob/master/test/to-markdown-test.js
    "paragraphs" in {
      "p" in {
        toMarkdown("<p>Lorem ipsum</p>") === "Lorem ipsum"
      }
      "Multiple ps" in {
        toMarkdown("<p>Lorem</p><p>ipsum</p>") === "Lorem\n\nipsum"
      }
    }
    "emphasis" in {
      "b" in {
        toMarkdown("<b>Hello world</b>") === "**Hello world**"
      }
      "strong" in {
        toMarkdown("<strong>Hello world</strong>") === "**Hello world**"
      }
      "i" in {
        toMarkdown("<i>Hello world</i>") === "_Hello world_"
      }
      "em" in {
        toMarkdown("<em>Hello world</em>") === "_Hello world_"
      }
      "Multiple ems" in {
        toMarkdown("<em>Hello</em> <em>world</em>") === "_Hello_ _world_"
      }
    }
    "code" in {
      toMarkdown("<code>print()</code>") === "`print()`"
    }
    "headings" in {
      "h1" in {
        toMarkdown("<h1>Hello world</h1>") === "# Hello world"
      }
      "h3" in {
        toMarkdown("<h3>Hello world</h3>") === "### Hello world"
      }
      "h6" in {
        toMarkdown("<h6>Hello world</h6>") === "###### Hello world"
      }
      "h4 with child" in {
        toMarkdown("<h4><i>Hello</i> world</h4>") === "#### _Hello_ world"
      }
      "invalid heading" in {
        toMarkdown("<h8>Hello world</h8>") ===
          // to-markdown doesn't indent
          """<h8>
            | Hello world
            |</h8>""".stripMargin
      }
    }
    "horizontal rules" in {
      "hr" in {
        toMarkdown("<hr />") === "* * *"
      }
      "open/closed hr" in {
        toMarkdown("<hr></hr>") === "* * *"
      }
    }
    "line breaks" in {
      toMarkdown("Hello<br />world") === "Hello  \nworld"
    }
    "images" in {
      "img with no alt" in {
        toMarkdown("""<img src="http://example.com/logo.png" />""") === "![](http://example.com/logo.png)"
      }
      "img with relative src" in {
        toMarkdown("""<img src=logo.png>""") === "![](logo.png)"
      }
      "img with alt" in {
        toMarkdown("""<img src=logo.png alt="Example logo">""") === "![Example logo](logo.png)"
      }
      "img no src" in {
        toMarkdown("<img>") === ""
      }
    }
    "anchors" in {
      "a" in {
        toMarkdown("""<a href="http://example.com/about">About us</a>""") === "[About us](http://example.com/about)"
      }
      "a with title" in {
        toMarkdown("""<a href="http://example.com/about" title="About this company">About us</a>""") === """[About us](http://example.com/about "About this company")"""
      }
      "a with no src" in {
        toMarkdown("""<a id="donuts3">About us</a>""") === """<a id="donuts3">About us</a>"""
      }
      "with a span" in {
        toMarkdown("""<a href="http://example.com/about"><span>About us</span></a>""") === "[<span>About us</span>](http://example.com/about)"
      }
    }
    "pre/code blocks" in {
      toMarkdown(
        """<pre><code>def hello_world
          |  # 42 &lt; 9001
          |  "Hello world!"
          |end</code></pre>""".stripMargin) ===
        """    def hello_world
          |      # 42 < 9001
          |      "Hello world!"
          |    end""".stripMargin
    }
    "pre/code blocks" in {
      "Multiple pre/code blocks" in {
        toMarkdown(
          """<pre><code>def foo
            |  # 42 &lt; 9001
            |  'Hello world!'
            |end</code></pre>
            |<p>next:</p>
            |<pre><code>def bar
            |  # 42 &lt; 9001
            |  'Hello world!'
            |end</code></pre>""".stripMargin) ===
          """    def foo
            |      # 42 < 9001
            |      'Hello world!'
            |    end
            |
            |next:
            |
            |    def bar
            |      # 42 < 9001
            |      'Hello world!'
            |    end""".stripMargin
      }
      "Plain pre" in {
        toMarkdown("<pre>preformatted</pre>") === "<pre>preformatted</pre>"
      }
    }
    "lists" in {
      "ol triggers are escaped" in {
        toMarkdown("1986. What a great season.") === "1986\\. What a great season."
      }
      "ol" in {
        toMarkdown("<ol>\n\t<li>Hello world</li>\n\t<li>Foo bar</li>\n</ol>") ===
          "1.  Hello world\n2.  Foo bar"
      }
      "ul" in {
        toMarkdown("<ul>\n\t<li>Hello world</li>\n\t<li>Foo bar</li>\n</ul>") ===
          "*   Hello world\n*   Foo bar"
      }
      "Multiple uls" in {
        toMarkdown(
          """<ul>
            |  <li>Hello world</li>
            |  <li>Lorem ipsum</li>
            |</ul>
            |<ul>
            |  <li>Hello world</li>
            |  <li>Lorem ipsum</li>
            |</ul>""".stripMargin) ===
          """*   Hello world
            |*   Lorem ipsum
            |
            |*   Hello world
            |*   Lorem ipsum""".stripMargin
      }
      "ul with p" in {
        toMarkdown("<ul><li><p>Hello world</p></li><li>Lorem ipsum</li></ul>") ===
          """*   Hello world
            |
            |*   Lorem ipsum""".stripMargin
      }
      "ol with multiple ps" in {
        toMarkdown(
          """<ol>
            |  <li>
            |    <p>This is a list item with two paragraphs.</p>
            |    <p>Vestibulum enim wisi, viverra nec, fringilla in, laoreet vitae, risus.</p>
            |  </li>
            |  <li>
            |    <p>Suspendisse id sem consectetuer libero luctus adipiscing.</p>
            |  </li>
            |</ol>
            |""".stripMargin) ===
          """1.  This is a list item with two paragraphs.
            |
            |    Vestibulum enim wisi, viverra nec, fringilla in, laoreet vitae, risus.
            |
            |2.  Suspendisse id sem consectetuer libero luctus adipiscing.""".stripMargin
      }
      "Nested uls" in {
        toMarkdown(
          """<ul>
            |  <li>This is a list item at root level</li>
            |  <li>This is another item at root level</li>
            |  <li>
            |    <ul>
            |      <li>This is a nested list item</li>
            |      <li>This is another nested list item</li>
            |      <li>
            |        <ul>
            |          <li>This is a deeply nested list item</li>
            |          <li>This is another deeply nested list item</li>
            |          <li>This is a third deeply nested list item</li>
            |        </ul>
            |      </li>
            |    </ul>
            |  </li>
            |  <li>This is a third item at root level</li>
            |</ul>
            |""".stripMargin) ===
          """*   This is a list item at root level
            |*   This is another item at root level
            |*   *   This is a nested list item
            |    *   This is another nested list item
            |    *   *   This is a deeply nested list item
            |        *   This is another deeply nested list item
            |        *   This is a third deeply nested list item
            |
            |*   This is a third item at root level""".stripMargin
      }
      "Nested ols" in {
        toMarkdown(
          """<ul>
            |  <li>This is a list item at root level</li>
            |  <li>This is another item at root level</li>
            |  <li>
            |    <ol>
            |      <li>This is a nested list item</li>
            |      <li>This is another nested list item</li>
            |      <li>
            |        <ul>
            |          <li>This is a deeply nested list item</li>
            |          <li>This is another deeply nested list item</li>
            |          <li>This is a third deeply nested list item</li>
            |        </ul>
            |      </li>
            |    </ol>
            |  </li>
            |  <li>This is a third item at root level</li>
            |</ul>""".stripMargin) ===
          //TODO: to-markdown doesn't have blank line
          """*   This is a list item at root level
            |*   This is another item at root level
            |*   1.  This is a nested list item
            |    2.  This is another nested list item
            |    3.  *   This is a deeply nested list item
            |        *   This is another deeply nested list item
            |        *   This is a third deeply nested list item
            |
            |*   This is a third item at root level""".stripMargin
      }
      "ul with blockquote" in {
        toMarkdown(
          """<ul>
            |  <li>
            |    <p>A list item with a blockquote:</p>
            |    <blockquote>
            |      <p>This is a blockquote inside a list item.</p>
            |    </blockquote>
            |  </li>
            |</ul>""".stripMargin) ===
          """*   A list item with a blockquote:
            |
            |    > This is a blockquote inside a list item.""".stripMargin
      }
    }
    "blockquotes" in {
      "blockquote with two ps" in {
        toMarkdown(
          """<blockquote>
            |  <p>This is a blockquote with two paragraphs.</p>
            |
            |  <p>Donec sit amet nisl.</p>
            |</blockquote>""".stripMargin) ===
          """> This is a blockquote with two paragraphs.
            |>
            |> Donec sit amet nisl."""
            .stripMargin
            .replaceAll("(?m)>$", "> ")
      }
      "Nested blockquotes" in {
        toMarkdown(
          """<blockquote>
            |  <p>This is the first level of quoting.</p>
            |
            |  <blockquote>
            |    <p>This is nested blockquote.</p>
            |  </blockquote>
            |
            |  <p>Back to the first level.</p>
            |</blockquote>""".stripMargin) ===
          """> This is the first level of quoting.
            |>
            |> > This is nested blockquote.
            |>
            |> Back to the first level."""
            .stripMargin
            .replaceAll("(?m)>$", "> ")
      }
      "html in blockquote" in {
        toMarkdown(
          """<blockquote>
            |  <h2>This is a header.</h2>
            |  <ol>
            |    <li>This is the first list item.</li>
            |    <li>This is the second list item.</li>
            |  </ol>
            |  <p>Here\'s some example code:</p>
            |  <pre><code>return 1 &lt; 2 ? shell_exec(\'echo $input | $markdown_script\') : 0;</code></pre>
            |</blockquote>""".stripMargin) ===
          """> ## This is a header.
            |>
            |> 1.  This is the first list item.
            |> 2.  This is the second list item.
            |>
            |> Here\'s some example code:
            |>
            |>     return 1 < 2 ? shell_exec(\'echo $input | $markdown_script\') : 0;"""
            .stripMargin
            .replaceAll("(?m)>$", "> ")
      }
    }
    "block-level" in {
      "divs separated by \\\\n\\\\n" in {
        toMarkdown("<div>Hello</div><div>world</div>") ===
          """<div>
            | Hello
            |</div>
            |
            |<div>
            | world
            |</div>""".stripMargin
      }
    }
    "block-level" in {
      toMarkdown("<div><em>hello</em></div>") ===
        """<div>
          | _hello_
          |</div>""".stripMargin
    }
    "comments" in {
      "comments removed" in {
        toMarkdown("<!-- comment -->") === ""
      }
    }
    "leading/trailing whitespace" in {
      "Whitespace between inline elements" in {
        toMarkdown("""<p>I <a href="http://example.com">need</a> <a href="http://www.example.com">more</a> spaces!</p>""") ===
          """I [need](http://example.com) [more](http://www.example.com) spaces!"""
      }
      //FIXME: document.outputSettings.prettyPrint(false) required
//      "Leading whitespace in h1" in {
//        toMarkdown("<h1>\n    lined Header text") === "# Header text"
//      }
      "Trailing whitespace in li" in {
        toMarkdown(
          """<ol>
            |  <li>Chapter One
            |    <ol>
            |      <li>Section One</li>
            |      <li>Section Two </li>
            |      <li>Section Three </li>
            |    </ol>
            |  </li>
            |  <li>Chapter Two</li>
            |  <li>Chapter Three  </li>
            |</ol>""".stripMargin) ===
          // to-markdown doesn't have blank line
          """1.  Chapter One
            |
            |    1.  Section One
            |    2.  Section Two
            |    3.  Section Three
            |
            |2.  Chapter Two
            |3.  Chapter Three""".stripMargin
        toMarkdown(
          """<ul>
            |  <li>
            |    Foo
            |  </li>
            |  <li>
            |    <strong>Bar </strong> </li>
            |  <li>Baz</li>
            |</ul>
            |<ol>
            |  <li> Hello
            |                      world
            |  </li>
            |</ol>""".stripMargin) ===
          //FIXME: the first blank line not present in the original to-markdown
          """*   Foo
            |
            |*   **Bar **
            |*   Baz
            |
            |1.  Hello
            |                          world""".stripMargin
//          """*   Foo
//            |*   **Bar**
//            |*   Baz
//            |
//            |1.  Hello world""".stripMargin
      }
      //FIXME: document.outputSettings.prettyPrint(false) required, maybe
//      "Whitespace in inline elements" in {
//        toMarkdown("Hello world.<em> Foo </em><strong>bar </strong>") === "Hello world. _Foo_ **bar**"
//      }
      "Whitespace and void elements" in {
        toMarkdown("""<h1><img src="image.png"> Hello world.</h1>""") === "# ![](image.png) Hello world."
      }
    }
    "blank" in {
      "Blank div" in {
        toMarkdown("<div></div>") must beEmpty
      }
      "Blank em" in {
        toMarkdown("<em></em>") must beEmpty
      }
      "Blank strong with br" in {
        toMarkdown("<strong><br></strong>") must beEmpty
      }
      "Blank a" in {
        toMarkdown("""<a href="#foo"></a>""") === "[](#foo)"
      }
    }
  }
}

