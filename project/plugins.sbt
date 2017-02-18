resolvers ++= Seq(
  Resolver.bintrayRepo("scala-js", "scala-js-releases")
)

addSbtPlugin("org.scala-js" % "sbt-scalajs" % "0.6.9")

addSbtPlugin("com.lihaoyi" % "workbench" % "0.2.3")

addSbtPlugin("me.lessis" % "bintray-sbt" % "0.3.0")
