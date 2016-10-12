resolvers += Classpaths.sbtPluginReleases
// sbt-ghpages
resolvers += "jgit-repo" at "http://download.eclipse.org/jgit/maven"

addSbtPlugin("org.scoverage"      %   "sbt-coveralls"         % "1.1.0")
addSbtPlugin("net.virtual-void"   %   "sbt-dependency-graph"  % "0.8.2")
addSbtPlugin("com.typesafe.sbt"   %   "sbt-ghpages"           % "0.5.4")
addSbtPlugin("com.jsuereth"       %   "sbt-pgp"               % "1.0.1")
addSbtPlugin("com.github.gseitz"  %   "sbt-release"           % "1.0.3")
addSbtPlugin("org.scoverage"      %   "sbt-scoverage"         % "1.3.5")
addSbtPlugin("com.timushev.sbt"   %   "sbt-updates"           % "0.2.0")
addSbtPlugin("org.scalastyle"     %%  "scalastyle-sbt-plugin" % "0.8.0")


