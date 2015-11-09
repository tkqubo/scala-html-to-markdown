# Scala HTML-to-Markdown
[![Maven Central][maven-image]][maven-link]
[![License: MIT](http://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
[![Circle CI][circle-ci-image]][circle-ci-link]
[![Coverage Status][coveralls-image]][coveralls-link]

An HTML to Markdown converter written in Scala, inspired by https://github.com/domchristie/to-markdown

## Installation

Assuming you are using Scala 2.11.x, add the following line to your build file:

```scala
libraryDependencies += "com.github.tkqubo" % "html-to-markdown" % "0.3.0"
```

## Quick Start

```scala
import com.github.tkqubo.html2md.Html2Markdown

val html = "<h1>introduction</h1><p>this is a <b>converter</b>.</p>"
val markdown = Html2Markdown.toMarkdown(html)
// markdown == "# introduction\n\nthis is a **converter**."
```

## API

**TBD**

### Writing your own rule

You can write your own HTML-to-markdown conversion rules.  They can be represented as a simple tuple

**TBD**

```scala
// <br>
('br -> "\n")

// <p>
('p -> { content: String => s"\n\n$content\n\n" })

// <em> or <i>
(Seq('em, 'i) -> { content: String => s"_${content}_" })

// <a> with href attr
(
  { e: Element => e.tagName == "a" && e.hasAttr("href") } ->
  { (text: String, e: Element) =>
    val titlePart = if (e.hasAttr("title")) s""" "${e.attr("title")}"""" else ""
    s"""[$text](${e.attr("href")}$titlePart)"""
  }
)
```

**TBD**

## Development

### Test

```bash
sbt test
```

or, with coverage

```bash
sbt clean coverage test
```

## Copyright

Copyright (c) 2015 tkqubo. See [LICENSE](LICENSE) for details.


[maven-image]:      https://maven-badges.herokuapp.com/maven-central/com.github.tkqubo/html-to-markdown/badge.svg
[maven-link]:       https://maven-badges.herokuapp.com/maven-central/com.github.tkqubo/html-to-markdown
[circle-ci-image]:  https://img.shields.io/circleci/project/tkqubo/scala-html-to-markdown.svg
[circle-ci-link]:   https://circleci.com/gh/tkqubo/scala-html-to-markdown
[coveralls-image]:  https://coveralls.io/repos/tkqubo/scala-html-to-markdown/badge.svg?branch=master&service=github
[coveralls-link]:   https://coveralls.io/github/tkqubo/scala-html-to-markdown?branch=master
