lazy val commonSettings = Seq(
    organization := "io.underscore",
    version      := "0.1-SNAPSHOT",
    scalaVersion := "2.11.5"
)

lazy val shared = (project in file("shared"))
  .settings(commonSettings: _*)
  .settings(name         := "shared")

lazy val js = (project in file("js"))
  .settings(commonSettings: _*)
  .settings(workbenchSettings : _*)
  .settings(name            := "js",
    persistLauncher         := true,
    persistLauncher in Test := false,
    bootSnippet             := "doodle.ScalaJSExample().main();",
    testFrameworks          += new TestFramework("utest.runner.Framework"),
    libraryDependencies    ++= Seq(
      "org.scalaz"   %% "scalaz-core"        % "7.1.0",
      "org.scala-js" %% "scalajs-dom_sjs0.6" % "0.7.0",
      "com.lihaoyi"  %%% "utest"             % "0.3.0" % "test"
    )
    //refreshBrowsers <<= refreshBrowsers.triggeredBy(packageJS in Compile)
  ).dependsOn(shared)



lazy val jvm = (project in file("jvm"))
  .settings(commonSettings: _*)
  .settings(name         := "jvm",
    initialCommands in console := """
      |import doodle.core._
      |import doodle.syntax._
      |import doodle.jvm._
      |import doodle.examples._
    """.trim.stripMargin
  ).dependsOn(shared)


lazy val doodleJVM = jvm

lazy val doodleJS = js

// Handy shortcuts:
//  - `run`       runs `doodleJVM/run`
//  - `console`   runs `doodleJVM/console`
//  - `test`      runs `doodleJVM/test` and then `doodleJS/test`
//  - `fastOptJS` runs `doodleJS/fastOptJS`

run     <<= run     in (jvm, Compile)

console <<= console in (js, Compile)
