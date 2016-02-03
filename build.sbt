enablePlugins(GitVersioning)

enablePlugins(GitBranchPrompt)

lazy val doodle = crossProject.
  crossType(DoodleCrossType).
  settings(
    name          := "doodle",
    organization  := "underscoreio",
    scalaVersion  := "2.11.7",
    scalacOptions += "-feature",
    licenses += ("Apache-2.0", url("http://apache.org/licenses/LICENSE-2.0"))
  ).jvmSettings(
    bintrayOrganization := Some("underscoreio"),
    bintrayPackageLabels := Seq("scala", "training", "creative-scala"),
    bintrayRepository := "training",
    licenses += ("Apache-2.0", url("http://apache.org/licenses/LICENSE-2.0")),
    // Release versioning:
    // version := "0.2.0",
    // Snapshot versioning:
    git.baseVersion := "0.2.0",
    git.formattedShaVersion := {
      git.gitHeadCommit.value map { sha =>
        git.baseVersion.value + "-" + sha.substring(0, 6) + "-snapshot"
      }
    },
    initialCommands in console := """
      |import doodle.core._
      |import doodle.core.Image._
      |import doodle.syntax._
      |import doodle.jvm.Java2DCanvas._
      |import doodle.backend.StandardInterpreter._
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
      "org.scala-js"              %%% "scalajs-dom" % "0.8.1",
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
