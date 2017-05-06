version in ThisBuild := "0.7.0"

val catsVersion = "0.9.0"

scalaVersion in ThisBuild := "2.12.1"

lazy val doodle = crossProject.
  crossType(DoodleCrossType).
  settings(
    name          := "doodle",
    organization  := "underscoreio",
    scalaVersion  := "2.12.1",
    scalaOrganization := "org.typelevel",
    scalacOptions ++= Seq("-feature", "-Xfatal-warnings", "-deprecation", "-unchecked", "-Ywarn-unused-import", "-Ypartial-unification"),
    scalacOptions in (Compile, console) := Seq("-feature", "-Xfatal-warnings", "-deprecation", "-unchecked", "-Ypartial-unification"),
    licenses += ("Apache-2.0", url("http://apache.org/licenses/LICENSE-2.0")),
    libraryDependencies ++= Seq(
       "org.typelevel" %% "cats" % catsVersion,
       "org.scalatest" %% "scalatest" % "3.0.1" % "test",
       "org.scalacheck" %% "scalacheck" % "1.12.4" % "test"
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
      |import doodle.jvm.FileFrame._
      |import doodle.jvm.Java2DFrame._
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
    //refreshBrowsers <<= refreshBrowsers.triggeredBy(packageJS in Compile)
    libraryDependencies ++= Seq(
      "org.typelevel" %%% "cats"        % catsVersion,
      "org.scala-js"  %%% "scalajs-dom" % "0.9.2",
      "com.lihaoyi"   %%% "scalatags"   % "0.6.3"
    )
  )

lazy val doodleJVM = doodle.jvm

lazy val doodleJS = doodle.js

run     <<= run     in (doodleJVM, Compile)

console <<= console in (doodleJVM, Compile)

publish <<= publish in (doodleJVM, Compile)
