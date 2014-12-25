lazy val root = project.in(file(".")).
  enablePlugins(ScalaJSPlugin)


name := "Doodle"

version := "0.1-SNAPSHOT"

scalaVersion := "2.11.2"

persistLauncher := true

persistLauncher in Test := false

libraryDependencies ++= Seq(
  "org.scalaz" %% "scalaz-core" % "7.1.0",
  "org.scala-js" %%% "scalajs-dom" % "0.7.0",
  "com.lihaoyi" %%% "utest" % "0.2.5-M3"
) 

workbenchSettings

utest.jsrunner.Plugin.utestJsSettings

bootSnippet := "doodle.ScalaJSExample().main();"

testFrameworks += new TestFramework("utest.runner.JvmFramework")

//refreshBrowsers <<= refreshBrowsers.triggeredBy(packageJS in Compile)
