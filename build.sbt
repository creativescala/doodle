version in ThisBuild := "0.8.2"

val catsVersion = "1.1.0"

name         in ThisBuild := "doodle"
organization in ThisBuild := "underscoreio"
scalaVersion in ThisBuild := "2.12.6"
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
       "org.scalatest"  %%% "scalatest" % "3.0.5" % "test",
       "org.scalacheck" %%% "scalacheck" % "1.14.0" % "test"
    ),
    bintrayRepository := "training"
  ).jvmSettings(
    libraryDependencies ++= Seq(
      "de.erichseifert.vectorgraphics2d" % "VectorGraphics2D" % "0.13"
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
    scalaJSUseMainModuleInitializer         := true,
    scalaJSUseMainModuleInitializer in Test := false,
    libraryDependencies ++= Seq(
      "org.scala-js"  %%% "scalajs-dom" % "0.9.6",
      "com.lihaoyi"   %%% "scalatags"   % "0.6.7"
    )
  )

lazy val doodleJVM = doodle.jvm

lazy val doodleJS = doodle.js.enablePlugins(WorkbenchPlugin)

console := { console.in(doodleJVM, Compile).value }
