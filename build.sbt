organization := "com.github.tkqubo"

name := "html-to-markdown"

val scala211 = "2.11.12"
val scala212 = "2.12.8"
val scala213 = "2.13.0"

scalaVersion := scala212

crossScalaVersions := Seq(scala211, scala212, scala213)

val specs2Ver = "4.6.0"
val jsoupVer = "1.12.1"

libraryDependencies ++= Seq(
  "org.jsoup" % "jsoup" % jsoupVer,
  "org.specs2" %% "specs2-core" % specs2Ver % Test,
  "org.specs2" %% "specs2-matcher" % specs2Ver % Test,
  "org.specs2" %% "specs2-matcher-extra" % specs2Ver % Test,
  "org.specs2" %% "specs2-mock" % specs2Ver % Test
)

javaOptions in Test ++= Seq(
  s"-Djava.util.Arrays.useLegacyMergeSort=true"
)

initialCommands := "import com.github.tkqubo.html2md._"

// sbt publish
publishArtifact in Test := false
publishMavenStyle := true
pomIncludeRepository := { _ => false }
pomExtra := (
  <url>https://github.com/tkqubo/scala-html-to-markdown</url>
    <licenses>
      <license>
        <name>MIT</name>
        <url>http://opensource.org/licenses/MIT</url>
      </license>
    </licenses>
    <developers>
      <developer>
        <id>tkqubo</id>
        <name>Takaichi Kubo</name>
        <url>https://github.com/tkqubo</url>
      </developer>
    </developers>
  )
publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (version.value.trim.endsWith("SNAPSHOT")) {
    Some("snapshots" at nexus + "content/repositories/snapshots")
  } else {
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
  }
}
useGpg := true

// sbt-release
releaseVersionBump := sbtrelease.Version.Bump.Minor
releasePublishArtifactsAction := PgpKeys.publishSigned.value

// disable using the Scala version in output paths and artifacts
crossPaths := false

// sbt-ghpages
enablePlugins(GhpagesPlugin)
git.remoteRepo := "git@github.com:tkqubo/scala-html-to-markdown.git"
