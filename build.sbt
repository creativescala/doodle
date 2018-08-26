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

organization := "noelwelsh"
name := "doodle"
scalaVersion := "2.12.4"

startYear := Some(2015)
licenses += ("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0.txt"))

enablePlugins(AutomateHeaderPlugin)

coursierUseSbtCredentials := true
coursierChecksums := Nil      // workaround for nexus sync bugs

addCompilerPlugin("org.spire-math" % "kind-projector" % "0.9.5" cross CrossVersion.binary)

libraryDependencies += "org.typelevel" %% "cats-effect" % "0.8"
libraryDependencies += "org.typelevel" %% "cats-core" % "1.0.1"

libraryDependencies += "io.monix" %% "minitest" % "2.0.0" % "test"
libraryDependencies += "io.monix" %% "minitest-laws" % "2.0.0" % "test"

testFrameworks += new TestFramework("minitest.runner.Framework")

initialCommands in console := """
      |import doodle.fx._
      |import doodle.fx.examples._
    """.trim.stripMargin

cleanupCommands in console := """
      |doodle.fx.engine.Application.stop()
    """.trim.stripMargin

// fork in (Compile, console) := true
