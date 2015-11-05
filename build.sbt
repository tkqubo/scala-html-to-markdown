name := "scala-html-to-markdown"

organization := "com.github.tkqubo"

//version := "0.0.1"

scalaVersion := "2.11.7"

val specs2V = "3.6.4"

libraryDependencies ++= Seq(
  "org.jsoup" % "jsoup" % "1.8.3",
  "org.specs2" %% "specs2-core" % specs2V % "test",
  "org.specs2" %% "specs2-matcher" % specs2V % "test",
  "org.specs2" %% "specs2-matcher-extra" % specs2V % "test",
  "org.specs2" %% "specs2-mock" % specs2V % "test"
)

initialCommands := "import com.github.tkqubo.html2md._"

