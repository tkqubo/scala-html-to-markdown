package com.github.tkqubo.html2md.converters

import com.github.tkqubo.html2md.Html2Markdown
import org.specs2.mutable.Specification

/**
  * Test class for [[GitHubFlavoredMarkdownConverter]]
  */
class GitHubFlavoredMarkdownConverterSpec
  extends Specification {
  val target = new Html2Markdown(MarkdownConverter.Gfm)
  def toMarkdown: (String) => String = target.toMarkdown

  // tests below are originally taken from https://github.com/domchristie/to-markdown/blob/master/test/gfm-test.js
  "Html2Markdown(MarkdownConverter.Gfm).toMarkdown" should {
    "line breaks" in {
      toMarkdown("<p>Hello<br>world</p>") === "Hello\nworld"
    }

    "strikethroughs" in {
      "del" in {
        toMarkdown("<del>Lorem ipsum</del>") === "~~Lorem ipsum~~"
      }
      "s" in {
        toMarkdown("<s>Lorem ipsum</s>") === "~~Lorem ipsum~~"
      }
      "strike" in {
        toMarkdown("<strike>Lorem ipsum</strike>") === "~~Lorem ipsum~~"
      }
    }

    "task lists" in {
      "Unchecked inputs" in {
        toMarkdown("<ul><li><input type=checkbox>Check Me!</li></ul>") === "*   [ ] Check Me!"
      }
      "Checked inputs" in {
        toMarkdown("<ul><li><input type=checkbox checked>Checked!</li></ul>") === "*   [x] Checked!"
      }
    }

    "tables" in {
      "Basic table" in {
        toMarkdown(
          """<table>
            |  <thead>
            |    <tr>
            |      <th>Column 1</th>
            |      <th>Column 2</th>
            |    </tr>
            |  </thead>
            |  <tbody>
            |    <tr>
            |      <td>Row 1, Column 1</td>
            |      <td>Row 1, Column 2</td>
            |    </tr>
            |    <tr>
            |      <td>Row 2, Column 1</td>
            |      <td>Row 2, Column 2</td>
            |    </tr>
            |  </tbody
            |</table>""".stripMargin) ===
          """|| Column 1 | Column 2 |
            || --- | --- |
            || Row 1, Column 1 | Row 1, Column 2 |
            || Row 2, Column 1 | Row 2, Column 2 |""".stripMargin
      }
    }
    "Cell alignment" in {
      toMarkdown(
        """<table>
          |  <thead>
          |    <tr>
          |      <th align="left">Column 1</th>
          |      <th align="center">Column 2</th>
          |      <th align="right">Column 3</th>
          |      <th align="foo">Column 4</th>
          |    </tr>
          |  </thead>
          |  <tbody>
          |    <tr>
          |      <td>Row 1, Column 1</td>
          |      <td>Row 1, Column 2</td>
          |      <td>Row 1, Column 3</td>
          |      <td>Row 1, Column 4</td>
          |    </tr>
          |    <tr>
          |      <td>Row 2, Column 1</td>
          |      <td>Row 2, Column 2</td>
          |      <td>Row 2, Column 3</td>
          |      <td>Row 2, Column 4</td>
          |    </tr>
          |  </tbody
          |</table>""".stripMargin) ===
        """|| Column 1 | Column 2 | Column 3 | Column 4 |
          || :-- | :-: | --: | --- |
          || Row 1, Column 1 | Row 1, Column 2 | Row 1, Column 3 | Row 1, Column 4 |
          || Row 2, Column 1 | Row 2, Column 2 | Row 2, Column 3 | Row 2, Column 4 |""".stripMargin
    }

    "fenced code blocks" in {
      toMarkdown(
        """<pre><code>This is a regular paragraph.
          |
          |&lt;table&gt;
          |    &lt;tr&gt;
          |        &lt;td&gt;Foo&lt;/td&gt;
          |    &lt;/tr&gt;
          |&lt;/table&gt;
          |
          |This is another regular paragraph.
          |</code></pre>""").stripMargin ===
        """```
          |This is a regular paragraph.
          |
          |<table>
          |    <tr>
          |        <td>Foo</td>
          |    </tr>
          |</table>
          |
          |This is another regular paragraph.
          |```""".stripMargin
    }

    "syntax highlighting" in {
      toMarkdown(
        """<div class="highlight highlight-html"><pre>&lt;<span class="pl-ent">table</span>&gt;
          |    &lt;<span class="pl-ent">tr</span>&gt;
          |        &lt;<span class="pl-ent">td</span>&gt;Foo&lt;/<span class="pl-ent">td</span>&gt;
          |    &lt;/<span class="pl-ent">tr</span>&gt;
          |&lt;/<span class="pl-ent">table</span>&gt;</pre></div>""".stripMargin) ===
        """```html
          |<table>
          |    <tr>
          |        <td>Foo</td>
          |    </tr>
          |</table>```""".stripMargin
    }
  }
}
