version in ThisBuild := "0.6.5"

val catsVersion = "0.6.0"

lazy val doodle = crossProject.
  crossType(DoodleCrossType).
  settings(
    name          := "doodle",
    organization  := "underscoreio",
    scalaVersion  := "2.11.8",
    scalacOptions ++= Seq("-feature", "-Xfatal-warnings", "-deprecation", "-unchecked", "-Ywarn-unused-import"),
    scalacOptions in (Compile, console) := Seq("-feature", "-Xfatal-warnings", "-deprecation", "-unchecked"),
    licenses += ("Apache-2.0", url("http://apache.org/licenses/LICENSE-2.0")),
    libraryDependencies ++= Seq(
       "org.typelevel" %% "cats" % catsVersion,
       "org.scalatest" %% "scalatest" % "2.2.6" % "test",
       "org.scalacheck" %% "scalacheck" % "1.12.5" % "test"
    )
  ).jvmSettings(
    libraryDependencies ++= Seq(
      "de.erichseifert.vectorgraphics2d" % "VectorGraphics2D" % "0.11"
    ),
    bintrayOrganization := Some("underscoreio"),
    bintrayPackageLabels := Seq("scala", "training", "creative-scala"),
    bintrayRepository := "training",
    licenses += ("Apache-2.0", url("http://apache.org/licenses/LICENSE-2.0")),
    initialCommands in console := """
      |import doodle.core._
      |import doodle.core.Image._
      |import doodle.random._
      |import doodle.syntax._
      |import doodle.jvm.FileCanvas._
      |import doodle.jvm.Java2DCanvas._
      |import doodle.backend.StandardInterpreter._
      |import doodle.backend.Formats._
      |import doodle.examples._
    """.trim.stripMargin,
    cleanupCommands in console := """
      |doodle.jvm.quit()
    """.trim.stripMargin
  ).jsSettings(
    workbenchSettings : _*
  ).jsSettings(
    persistLauncher         := true,
    persistLauncher in Test := false,
    bootSnippet             := """
      |doodle.ScalaJSExample().main();
    """.trim.stripMargin,
    testFrameworks          += new TestFramework("utest.runner.Framework"),
    //refreshBrowsers <<= refreshBrowsers.triggeredBy(packageJS in Compile)
    libraryDependencies ++= Seq(
      "org.typelevel"             %%% "cats"        % catsVersion,
      "org.scala-js"              %%% "scalajs-dom" % "0.9.0",
      "com.lihaoyi"               %%% "scalatags"   % "0.5.5",
      "com.lihaoyi"               %%% "utest"       % "0.3.0" % "test",
      "com.github.japgolly.nyaya" %%% "nyaya-test"  % "0.5.3" % "test"
    )
  )

lazy val doodleJVM = doodle.jvm

lazy val doodleJS = doodle.js

run     <<= run     in (doodleJVM, Compile)

console <<= console in (doodleJVM, Compile)

publish <<= publish in (doodleJVM, Compile)
