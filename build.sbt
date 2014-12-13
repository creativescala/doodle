lazy val root = project.in(file(".")).
  enablePlugins(ScalaJSPlugin)


name := "Doodle"

version := "0.1-SNAPSHOT"

scalaVersion := "2.11.2"

persistLauncher := true

persistLauncher in Test := false

libraryDependencies ++= Seq(
  "org.scalaz" %% "scalaz-core" % "7.1.0",
  "org.scala-js" %%% "scalajs-dom" % "0.7.0"
) 

workbenchSettings

bootSnippet := "doodle.ScalaJSExample().main();"

//refreshBrowsers <<= refreshBrowsers.triggeredBy(packageJS in Compile)
