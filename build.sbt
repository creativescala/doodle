/*
 * Copyright 2017 Noel Welsh
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
scalaVersion in ThisBuild := "2.12.8"

// enablePlugins(AutomateHeaderPlugin)

coursierUseSbtCredentials := true
coursierChecksums := Nil      // workaround for nexus sync bugs


lazy val commonSettings = Seq(
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
  licenses := List("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0.txt")),

  addCompilerPlugin("org.spire-math" % "kind-projector" % "0.9.9" cross CrossVersion.binary)
)

lazy val root = crossProject
  .in(file("."))
  .settings(commonSettings,
            moduleName := "doodle",
            paradoxTheme := Some(builtinParadoxTheme("generic")),
            unidocProjectFilter in ( ScalaUnidoc, unidoc ) :=
              inAnyProject -- inProjects( animateJs, coreJs, exploreJs, imageJs, svgJs, turtleJs ))
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
      |import doodle.animate.syntax._
      |import doodle.animate.java2d._
      |import doodle.animate.examples._
      |import doodle.explore.syntax._
      |import doodle.explore.java2d._
      |import doodle.explore.java2d.examples._
      |import doodle.core._
    """.trim.stripMargin,
    cleanupCommands in console := """
      |doodle.java2d.effect.Java2dRenderer.stop()
    """.trim.stripMargin
  )
  .enablePlugins(ScalaUnidocPlugin)
lazy val rootJvm = root.jvm
  .dependsOn(animateJvm, coreJvm, java2d, exploreJvm, imageJvm, svgJvm, turtleJvm)
  .aggregate(animateJvm, coreJvm, java2d, exploreJvm, imageJvm, svgJvm, turtleJvm)
lazy val rootJs = root.js
  .dependsOn(animateJs, coreJs, exploreJs, imageJs, svgJs, turtleJs)
  .aggregate(animateJs, coreJs, exploreJs, imageJs, svgJs, turtleJs)


lazy val core = crossProject
  .in(file("core"))
  .settings(commonSettings,
            moduleName := "doodle-core")

lazy val coreJvm = core.jvm
lazy val coreJs  = core.js


lazy val doc = project
  .in(file("doc"))
  .enablePlugins(ParadoxPlugin)


lazy val java2d = project
  .in(file("java2d"))
  .settings(commonSettings,
            moduleName := "doodle-java2d")
  .dependsOn(coreJvm)

lazy val svg = crossProject
  .in(file("svg"))
//.enablePlugins(WorkbenchPlugin)
  .settings(commonSettings,
            moduleName := "doodle-svg",
            libraryDependencies += Dependencies.scalaTags.value
//            workbenchDefaultRootObject := Some(("svg/example.html", "svg/"))
            )

lazy val svgJvm = svg.jvm.dependsOn(coreJvm)
lazy val svgJs  = svg.js.dependsOn(coreJs)


lazy val image = crossProject
  .in(file("image"))
  .settings(commonSettings,
            moduleName := "doodle-image")

lazy val imageJvm = image.jvm.dependsOn(coreJvm, java2d)
lazy val imageJs  = image.js.dependsOn(coreJs)


lazy val animate = crossProject
  .in(file("animate"))
  .settings(commonSettings,
            libraryDependencies += Dependencies.monix.value,
            moduleName := "doodle-animate")

lazy val animateJvm = animate.jvm.dependsOn(coreJvm, java2d)
lazy val animateJs  = animate.js.dependsOn(coreJs)


lazy val explore = crossProject
  .in(file("explore"))
  .settings(commonSettings,
            libraryDependencies += Dependencies.magnolia.value,
            moduleName := "doodle-explore")
  .dependsOn(core, animate)

lazy val exploreJvm = explore.jvm.dependsOn(coreJvm, animateJvm)
lazy val exploreJs  = explore.js.dependsOn(coreJs, animateJs)


lazy val turtle = crossProject
  .in(file("turtle"))
  .settings(commonSettings,
            moduleName := "doodle-turtle")

lazy val turtleJvm = turtle.jvm.dependsOn(coreJvm, imageJvm)
lazy val turtleJs  = turtle.js.dependsOn(coreJs, imageJs)
