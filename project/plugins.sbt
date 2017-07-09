// Comment to get more information during initialization
logLevel := Level.Warn

// The Typesafe repository 
resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

// fast dependency download
addSbtPlugin("io.get-coursier" % "sbt-coursier" % "1.0.0-RC6")

// better compiler error messages
addSbtPlugin("org.duhemm" % "sbt-errors-summary" % "0.3.0")

// Use the Play sbt plugin for Play projects
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.6.1")

//addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.7.0")

addSbtPlugin("com.typesafe.sbt" % "sbt-less" % "1.1.1")