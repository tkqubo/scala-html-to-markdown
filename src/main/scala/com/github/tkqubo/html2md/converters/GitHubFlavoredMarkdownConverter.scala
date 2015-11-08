package com.github.tkqubo.html2md.converters

import org.jsoup.nodes.Element

class GitHubFlavoredMarkdownConverter extends MarkdownConverter(Seq(
  'br -> "\n",

  Seq('del, 's, 'strike) -> { content: String => s"~~$content~~" }
))
