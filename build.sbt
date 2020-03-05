/*
 * Copyright 2015-2020 Noel Welsh
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
lazy val scala212 = "2.12.10"
lazy val scala213 = "2.13.1"
lazy val supportedScalaVersions = List(scala212, scala213)

ThisBuild / scalaVersion := scala213
ThisBuild / useSuperShell := false

// enablePlugins(AutomateHeaderPlugin)

coursierUseSbtCredentials := true
coursierChecksums := Nil // workaround for nexus sync bugs

lazy val commonSettings = Seq(
  crossScalaVersions := supportedScalaVersions,
  libraryDependencies ++= Seq(
    Dependencies.catsCore.value,
    Dependencies.catsEffect.value,
    Dependencies.catsFree.value,
    Dependencies.miniTest.value,
    Dependencies.miniTestLaws.value
  ),
  testFrameworks += new TestFramework("minitest.runner.Framework"),
  credentials += Credentials(Path.userHome / ".sbt" / "sonatype_credential"),
  startYear := Some(2015),
  licenses := List(
    "Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0.txt")
  ),
  addCompilerPlugin(
    "org.typelevel" % "kind-projector" % "0.10.3" cross CrossVersion.binary
  )
)

lazy val root = crossProject
  .in(file("."))
  .settings(
    commonSettings,
    // crossScalaVersions must be set to Nil on the aggregating project
    crossScalaVersions := Nil,
    moduleName := "doodle",
    paradoxTheme := Some(builtinParadoxTheme("generic")),
    unidocProjectFilter in (ScalaUnidoc, unidoc) :=
      inAnyProject -- inProjects(
        coreJs,
        interactJs,
        exploreJs,
        imageJs,
        plotJs,
        reactorJs,
        svgJs,
        turtleJs
      )
  )
  .jvmSettings(
    initialCommands in console := """
      |import cats.instances.all._
      |import doodle.java2d._
      |import doodle.syntax._
      |import doodle.effect.Writer._
      |import doodle.examples._
      |import doodle.image._
      |import doodle.image.syntax._
      |import doodle.image.examples._
      |import doodle.interact.syntax._
      |import doodle.explore.syntax._
      |import doodle.core._
    """.trim.stripMargin,
    cleanupCommands in console := """
      |doodle.java2d.effect.Java2dRenderer.stop()
    """.trim.stripMargin
  )
  .enablePlugins(ScalaUnidocPlugin)
lazy val rootJvm = root.jvm
  .dependsOn(
    coreJvm,
    java2d,
    exploreJvm,
    imageJvm,
    interactJvm,
    reactorJvm,
    svgJvm,
    turtleJvm,
    golden
  )
  .aggregate(
    coreJvm,
    java2d,
    exploreJvm,
    imageJvm,
    interactJvm,
    reactorJvm,
    svgJvm,
    turtleJvm,
    golden
  )
lazy val rootJs = root.js
  .dependsOn(coreJs, exploreJs, imageJs, interactJs, reactorJs, svgJs, turtleJs)
  .aggregate(coreJs, exploreJs, imageJs, interactJs, reactorJs, svgJs, turtleJs)

lazy val core = crossProject
  .in(file("core"))
  .settings(commonSettings, moduleName := "doodle-core")

lazy val coreJvm = core.jvm
lazy val coreJs = core.js

lazy val copyFiles1 = taskKey[Unit]("SBT is a pile of bullshit")
copyFiles1 := {}

// Why can't I mix normal code and Task .value in the same task?
// When I do the normal code doesn't run. SBT is a pile of shit
lazy val copyScalaDoc = taskKey[Unit]("SBT is bullshit")
copyScalaDoc := {
  println("Copying Scaladoc")
  sbt.io.IO.copyDirectory(
    file("jvm/target/scala-2.12/unidoc/"),
    file("docs/src/main/mdoc/api")
  )
}
lazy val copyFinalDoc = taskKey[Unit]("SBT is a pile of shit")
copyFinalDoc := {
  println("Copying documentation to docs/target/docs/site/main")
  sbt.io.IO.copyDirectory(
    file("docs/target/paradox/site/main"),
    file("docs/target/docs")
  )
}
lazy val documentation = taskKey[Unit]("Generate documentation")
documentation :=
  Def
    .sequential(
      (rootJvm / Compile / unidoc),
      copyScalaDoc,
      (docs / Compile / mdoc).toTask(""),
      (docs / Compile / paradox).toTask,
      copyFinalDoc
    )
    .value
lazy val docs = project
  .in(file("docs"))
  .settings(
    mdocIn := file("docs/src/main/mdoc"),
    mdocOut := file("docs/src/main/paradox"),
    (paradoxProperties in Compile) ++= Map(
      "scaladoc.base_url" -> ".../api/"
    ),
    mdocVariables := Map("VERSION" -> version.value)
  )
  .enablePlugins(MdocPlugin, ParadoxPlugin)
  .dependsOn(rootJvm)

lazy val interact = crossProject
  .in(file("interact"))
  .settings(
    commonSettings,
    libraryDependencies += Dependencies.monix.value,
    moduleName := "doodle-interact"
  )

lazy val interactJvm =
  interact.jvm.dependsOn(coreJvm % "compile->compile;test->test")
lazy val interactJs =
  interact.js.dependsOn(coreJs % "compile->compile;test->test")

lazy val java2d = project
  .in(file("java2d"))
  .settings(
    commonSettings,
    moduleName := "doodle-java2d",
    libraryDependencies += "de.erichseifert.vectorgraphics2d" % "VectorGraphics2D" % "0.13"
  )
  .dependsOn(coreJvm, exploreJvm, interactJvm)

lazy val image = crossProject
  .in(file("image"))
  .settings(commonSettings, moduleName := "doodle-image")

lazy val imageJvm = image.jvm.dependsOn(coreJvm, java2d)
lazy val imageJs = image.js.dependsOn(coreJs)

lazy val plot = crossProject
  .in(file("plot"))
  .settings(commonSettings, moduleName := "doodle-plot")

lazy val plotJvm = plot.jvm.dependsOn(coreJvm, interactJvm)
lazy val plotJs = plot.js.dependsOn(coreJs, interactJs)

lazy val explore = crossProject
  .in(file("explore"))
  .settings(
    commonSettings,
    libraryDependencies += Dependencies.magnolia.value,
    moduleName := "doodle-explore"
  )
  .dependsOn(core, interact)

lazy val exploreJvm = explore.jvm.dependsOn(coreJvm, interactJvm)
lazy val exploreJs = explore.js.dependsOn(coreJs, interactJs)

lazy val svg = crossProject
  .in(file("svg"))
  .settings(
    commonSettings,
    moduleName := "doodle-svg",
    libraryDependencies += Dependencies.scalaTags.value
  )

lazy val svgJvm =
  svg.jvm.dependsOn(coreJvm % "compile->compile;test->test", interactJvm)
lazy val svgJs =
  svg.js.dependsOn(coreJs % "compile->compile;test->test", interactJs)

lazy val turtle = crossProject
  .in(file("turtle"))
  .settings(commonSettings, moduleName := "doodle-turtle")

lazy val turtleJvm = turtle.jvm.dependsOn(coreJvm, imageJvm)
lazy val turtleJs = turtle.js.dependsOn(coreJs, imageJs)

lazy val reactor = crossProject
  .in(file("reactor"))
  .settings(
    commonSettings,
    libraryDependencies += Dependencies.monix.value,
    moduleName := "doodle-reactor"
  )

lazy val reactorJvm =
  reactor.jvm.dependsOn(coreJvm, java2d, imageJvm, interactJvm)
lazy val reactorJs = reactor.js.dependsOn(coreJs, svgJs, imageJs, interactJs)

// Just for testing
lazy val golden = project
  .in(file("golden"))
  .settings(
    commonSettings,
    moduleName := "doodle-golden",
    libraryDependencies += Dependencies.munit.value,
    testFrameworks += new TestFramework("munit.Framework")
  )
  .dependsOn(coreJvm, imageJvm, interactJvm, java2d)

// To avoid including this in the core build
lazy val examples = crossProject
  .in(file("examples"))
  .settings(
    commonSettings,
    moduleName := "doodle-examples"
  )

lazy val examplesJvm = examples.jvm.dependsOn(coreJvm, interactJvm, imageJvm, java2d, svgJvm)
lazy val examplesJs = examples.js.dependsOn(coreJs, interactJs, imageJs, svgJs)
