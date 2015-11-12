resolvers += Classpaths.sbtPluginReleases

addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.3.3")

addSbtPlugin("com.jsuereth" % "sbt-pgp" % "1.0.0")

addSbtPlugin("org.scalastyle" %% "scalastyle-sbt-plugin" % "0.7.0")

//addSbtPlugin("org.scoverage" % "sbt-coveralls" % "1.0.0")

//TODO: Refer master branch due to the bug below:
// https://github.com/scoverage/sbt-coveralls/issues/62
lazy val root = project.in(file(".")).dependsOn(githubRepo)
lazy val githubRepo = uri("git://github.com/scoverage/sbt-coveralls.git")

addSbtPlugin("com.github.gseitz" % "sbt-release" % "1.0.0")

// sbt-ghpages
resolvers += "jgit-repo" at "http://download.eclipse.org/jgit/maven"
addSbtPlugin("com.typesafe.sbt" % "sbt-ghpages" % "0.5.4")
