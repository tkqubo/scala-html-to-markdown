resolvers += Classpaths.sbtPluginReleases
// sbt-ghpages
resolvers += "jgit-repo" at "http://download.eclipse.org/jgit/maven"

addSbtPlugin("org.scoverage"      %   "sbt-coveralls"         % "1.2.2")
addSbtPlugin("com.typesafe.sbt"   %   "sbt-ghpages"           % "0.6.2")
addSbtPlugin("com.jsuereth"       %   "sbt-pgp"               % "1.1.0")
addSbtPlugin("com.github.gseitz"  %   "sbt-release"           % "1.0.6")
addSbtPlugin("org.scoverage"      %   "sbt-scoverage"         % "1.5.1")
addSbtPlugin("com.timushev.sbt"   %   "sbt-updates"           % "0.3.1")
addSbtPlugin("org.scalastyle"     %%  "scalastyle-sbt-plugin" % "1.0.0")


