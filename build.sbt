import bintray.Keys._

enablePlugins(GitVersioning)

enablePlugins(GitBranchPrompt)

lazy val doodle = crossProject.
  crossType(DoodleCrossType).
  settings(
    name          := "doodle",
    organization  := "underscoreio",
    scalaVersion  := "2.11.5",
    scalacOptions += "-feature"
  ).jvmSettings(
    bintrayPublishSettings : _*
  ).jvmSettings(
    bintrayOrganization in bintray := Some("underscoreio"),
    packageLabels in bintray := Seq("scala", "training", "creative-scala"),
    repository in bintray := "training",
    licenses += ("Apache-2.0", url("http://apache.org/licenses/LICENSE-2.0")),
    // Versioning based on Git releases:
    git.useGitDescribe := true,
    git.baseVersion := "0.1.0",
    git.gitTagToVersionNumber := { tag =>
      Option(tag) filter (_ matches "[0-9][.][0.9][.][0-9]")
    },
    initialCommands in console := """
      |import doodle.core._
      |import doodle.syntax._
      |import doodle.jvm._
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
    libraryDependencies    ++= Seq(
      "org.scalaz"                %%  "scalaz-core" % "7.1.0",
      "org.scala-js"              %%% "scalajs-dom" % "0.7.0",
      "com.lihaoyi"               %%% "utest"       % "0.3.0" % "test",
      "com.github.japgolly.nyaya" %%% "nyaya-test"  % "0.5.3" % "test"
    )
    //refreshBrowsers <<= refreshBrowsers.triggeredBy(packageJS in Compile)
  )

lazy val doodleJVM = doodle.jvm

lazy val doodleJS = doodle.js

run     <<= run     in (doodleJVM, Compile)

console <<= console in (doodleJVM, Compile)

publish <<= publish in (doodleJVM, Compile)
