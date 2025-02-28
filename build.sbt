/*
 * Copyright 2015-2020 Creative Scala
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
import scala.sys.process.*
import laika.config.LinkConfig
import laika.config.ApiLinks
import laika.theme.Theme

ThisBuild / tlBaseVersion := "0.28" // your current series x.y

Global / onChangedBuildSource := ReloadOnSourceChanges

ThisBuild / organization := "org.creativescala"
ThisBuild / organizationName := "Creative Scala"
ThisBuild / startYear := Some(2015)
ThisBuild / licenses := Seq(License.Apache2)
ThisBuild / developers := List(
  // your GitHub handle and name
  tlGitHubDev("noelwelsh", "Noel Welsh")
)

ThisBuild / sonatypeCredentialHost := xerial.sbt.Sonatype.sonatypeLegacy

lazy val scala3 = "3.3.4"

ThisBuild / crossScalaVersions := List(scala3)
ThisBuild / scalaVersion := scala3
ThisBuild / useSuperShell := false
ThisBuild / semanticdbEnabled := true
ThisBuild / semanticdbVersion := scalafixSemanticdb.revision
ThisBuild / tlSitePublishBranch := Some("main")

// Run this (build) to do everything involved in building the project
commands += Command.command("build") { state =>
  "clean" ::
    "compile" ::
    "test" ::
    "golden/test" ::
    "scalafixAll" ::
    "scalafmtAll" ::
    "headerCreateAll" ::
    "githubWorkflowGenerate" ::
    "dependencyUpdates" ::
    "reload plugins; dependencyUpdates; reload return" ::
    "docs / tlSite" ::
    state
}

lazy val css = taskKey[Unit]("Build the CSS")

lazy val commonSettings = Seq(
  // This is needed when running examples
  Compile / run / fork := true,
  libraryDependencies ++= Seq(
    Dependencies.munit.value,
    Dependencies.munitScalaCheck.value
  ),
  startYear := Some(2015),
  licenses := List(
    "Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0.txt")
  )
)

lazy val root = crossProject(JSPlatform, JVMPlatform)
  .in(file("."))
  .settings(moduleName := "doodle")
lazy val rootJvm =
  root.jvm
    .settings(mimaPreviousArtifacts := Set.empty)
    .dependsOn(
      core.jvm,
      algebra.jvm,
      java2d,
      image.jvm,
      interact.jvm,
      reactor.jvm,
      svg.jvm,
      turtle.jvm
    )
    .aggregate(
      core.jvm,
      algebra.jvm,
      java2d,
      image.jvm,
      interact.jvm,
      reactor.jvm,
      svg.jvm,
      turtle.jvm,
      golden,
      unidocs
    )
lazy val rootJs =
  root.js
    .settings(mimaPreviousArtifacts := Set.empty)
    .dependsOn(
      canvas,
      core.js,
      algebra.js,
      image.js,
      interact.js,
      reactor.js,
      svg.js,
      turtle.js
    )
    .aggregate(
      canvas,
      core.js,
      algebra.js,
      image.js,
      interact.js,
      reactor.js,
      svg.js,
      turtle.js,
      golden,
      unidocs
    )

lazy val core = crossProject(JSPlatform, JVMPlatform)
  .in(file("core"))
  .settings(
    commonSettings,
    libraryDependencies ++= Seq(
      Dependencies.catsCore.value,
      Dependencies.catsFree.value
    ),
    moduleName := "doodle-core"
  )

lazy val algebra = crossProject(JSPlatform, JVMPlatform)
  .in(file("algebra"))
  .settings(
    commonSettings,
    libraryDependencies ++= Seq(
      Dependencies.catsEffect.value,
    ),
    moduleName := "doodle-algebra"
  )
lazy val algebraJvm =
  algebra.jvm.dependsOn(core.jvm % "test->test;compile->compile")
lazy val algebraJs =
  algebra.js.dependsOn(core.js % "test->test;compile->compile")

lazy val docs =
  project
    .in(file("docs"))
    .settings(
      laikaConfig := laikaConfig.value.withConfigValue(
        LinkConfig.empty
          .addApiLinks(
            ApiLinks(baseUri =
              "https://javadoc.io/doc/org.creativescala/doodle-docs_3/latest/"
            )
          )
      ),
      mdocIn := file("docs/src/pages"),
      css := {
        val src = file("docs/src/css")
        val dest1 = mdocOut.value
        val dest2 = (laikaSite / target).value
        "npm install" !
        val cmd1 =
          s"npx tailwindcss -i ${src.toString}/creative-scala.css -o ${dest1.toString}/creative-scala.css"
        val cmd2 =
          s"npx tailwindcss -i ${src.toString}/creative-scala.css -o ${dest2.toString}/creative-scala.css"
        println(cmd1)
        cmd1 !

        println(cmd2)
        cmd2 !
      },
      Laika / sourceDirectories ++=
        Seq(
          file("docs/src/templates"),
          (examples.js / Compile / fastOptJS / artifactPath).value
            .getParentFile() / s"${(examples.js / moduleName).value}-fastopt"
        ),
      laikaTheme := Theme.empty,
      laikaExtensions ++= Seq(
        laika.format.Markdown.GitHubFlavor,
        laika.config.SyntaxHighlighting,
        CreativeScalaDirectives
      ),
      tlSite := Def
        .sequential(
          (examples.js / Compile / fastLinkJS),
          (Compile / run).toTask(""),
          mdoc.toTask(""),
          css,
          laikaSite
        )
        .value,
      tlFatalWarnings := false
    )
    .enablePlugins(TypelevelSitePlugin)
    .dependsOn(algebra.jvm, image.jvm)

lazy val unidocs = project
  .in(file("unidocs"))
  .enablePlugins(TypelevelUnidocPlugin) // also enables the ScalaUnidocPlugin
  .settings(
    name := "doodle-docs",
    ScalaUnidoc / unidoc / unidocProjectFilter :=
      inAnyProject -- inProjects(
        docs,
        algebra.js,
        interact.js,
        examples.js,
        golden,
        image.js,
        reactor.js,
        svg.js,
        turtle.js
      )
  )

lazy val interact = crossProject(JSPlatform, JVMPlatform)
  .in(file("interact"))
  .settings(
    commonSettings,
    libraryDependencies += Dependencies.fs2.value,
    moduleName := "doodle-interact"
  )
  .jvmConfigure(_.dependsOn(algebra.jvm % "compile->compile;test->test"))
  .jsConfigure(_.dependsOn(algebra.js % "compile->compile;test->test"))

lazy val java2d = project
  .in(file("java2d"))
  .settings(
    commonSettings,
    moduleName := "doodle-java2d",
    libraryDependencies ++= Seq(
      "de.erichseifert.vectorgraphics2d" % "VectorGraphics2D" % "0.13",
      Dependencies.munitCatsEffect.value,
      Dependencies.fs2.value
    )
  )
  .dependsOn(algebra.jvm, interact.jvm)

lazy val image = crossProject(JSPlatform, JVMPlatform)
  .in(file("image"))
  .settings(commonSettings, moduleName := "doodle-image")
  .jvmConfigure(_.dependsOn(algebra.jvm, java2d))
  .jsConfigure(_.dependsOn(algebra.js))

lazy val turtle = crossProject(JSPlatform, JVMPlatform)
  .in(file("turtle"))
  .settings(commonSettings, moduleName := "doodle-turtle")
  .jvmConfigure(_.dependsOn(algebra.jvm, image.jvm))
  .jsConfigure(_.dependsOn(algebra.js, image.js))

lazy val reactor = crossProject(JSPlatform, JVMPlatform)
  .in(file("reactor"))
  .settings(
    commonSettings,
    libraryDependencies += Dependencies.fs2.value,
    moduleName := "doodle-reactor"
  )
  .jvmConfigure(_.dependsOn(algebra.jvm, java2d, image.jvm, interact.jvm))
  .jsConfigure(_.dependsOn(algebra.js, image.js, interact.js))

lazy val svg = crossProject(JSPlatform, JVMPlatform)
  .in(file("svg"))
  .settings(
    commonSettings,
    libraryDependencies ++= Seq(
      Dependencies.scalatags.value,
      Dependencies.munitCatsEffect.value
    ),
    moduleName := "doodle-svg"
  )
  .dependsOn(algebra, interact)
  .jvmConfigure(_.dependsOn(java2d))

lazy val canvas = project
  .in(file("canvas"))
  .settings(
    commonSettings,
    libraryDependencies += Dependencies.scalajsDom.value,
    moduleName := "doodle-canvas"
  )
  .dependsOn(algebra.js)
  .enablePlugins(ScalaJSPlugin)

// Just for testing
lazy val golden = project
  .in(file("golden"))
  .settings(
    commonSettings,
    moduleName := "doodle-golden",
    libraryDependencies ++= Seq(
      Dependencies.munit.value,
      Dependencies.batik.value
    ),
    // We don't publish golden so we don't need to do MiMA checks on it.
    mimaPreviousArtifacts := Set.empty,
    testFrameworks += new TestFramework("munit.Framework")
  )
  .dependsOn(algebra.jvm, image.jvm, interact.jvm, java2d)

// To avoid including this in the core build
lazy val examples = crossProject(JSPlatform, JVMPlatform)
  .in(file("examples"))
  .settings(
    commonSettings,
    moduleName := "doodle-examples"
  )
  .jvmConfigure(
    _.settings(mimaPreviousArtifacts := Set.empty)
      .dependsOn(algebra.jvm, java2d, image.jvm, interact.jvm)
  )
  .jsConfigure(
    _.settings(mimaPreviousArtifacts := Set.empty)
      .dependsOn(algebra.js, canvas, image.js, interact.js)
  )
  .dependsOn(svg)
