version in ThisBuild := "0.8.2"

val catsVersion = "1.0.0-MF"

name         in ThisBuild := "doodle"
organization in ThisBuild := "underscoreio"
scalaVersion in ThisBuild := "2.12.2"
bintrayOrganization in ThisBuild := Some("underscoreio")
bintrayPackageLabels in ThisBuild := Seq("scala", "training", "creative-scala")
licenses in ThisBuild += ("Apache-2.0", url("http://apache.org/licenses/LICENSE-2.0"))

lazy val root = project.in(file(".")).
  aggregate(doodleJS, doodleJVM).
  settings(
    publish := {},
    publishLocal := {},
    bintrayRepository := "training"
  )

lazy val doodle = crossProject.
  crossType(DoodleCrossType).
  settings(
    scalacOptions ++= Seq("-feature", "-Xfatal-warnings", "-deprecation", "-unchecked", "-Ywarn-unused-import", "-Ypartial-unification"),
    scalacOptions in (Compile, console) := Seq("-feature", "-Xfatal-warnings", "-deprecation", "-unchecked", "-Ypartial-unification"),
    licenses += ("Apache-2.0", url("http://apache.org/licenses/LICENSE-2.0")),
    libraryDependencies ++= Seq(
       "org.typelevel"  %%% "cats-core" % catsVersion,
       "org.typelevel"  %%% "cats-free" % catsVersion,
       "org.scalatest"  %%% "scalatest" % "3.0.2" % "test",
       "org.scalacheck" %%% "scalacheck" % "1.13.5" % "test"
    ),
    bintrayRepository := "training"
  ).jvmSettings(
    libraryDependencies ++= Seq(
      "de.erichseifert.vectorgraphics2d" % "VectorGraphics2D" % "0.11"
    ),
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
    scalaJSUseMainModuleInitializer         := true,
    scalaJSUseMainModuleInitializer in Test := false,
    bootSnippet                             := """
      |doodle.ScalaJSExample().main();
    """.trim.stripMargin,
    //refreshBrowsers <<= refreshBrowsers.triggeredBy(packageJS in Compile)
    libraryDependencies ++= Seq(
      "org.scala-js"  %%% "scalajs-dom" % "0.9.2",
      "com.lihaoyi"   %%% "scalatags"   % "0.6.3"
    )
  )

lazy val doodleJVM = doodle.jvm

lazy val doodleJS = doodle.js

console := { console.in(doodleJVM, Compile).value }
