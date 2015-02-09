lazy val doodle = crossProject.
  crossType(DoodleCrossType).
  settings(
    name         := "doodle",
    version      := "0.1-SNAPSHOT",
    scalaVersion := "2.11.5"
  ).jsSettings(
    workbenchSettings : _*
  ).jsSettings(
    persistLauncher         := true,
    persistLauncher in Test := false,
    bootSnippet             := "doodle.ScalaJSExample().main();",
    testFrameworks          += new TestFramework("utest.runner.Framework"),
    libraryDependencies    ++= Seq(
      "org.scalaz"   %% "scalaz-core"  % "7.1.0",
      "org.scala-js" %%% "scalajs-dom" % "0.7.0",
      "com.lihaoyi"  %%% "utest"       % "0.3.0" % "test"
    )
    //refreshBrowsers <<= refreshBrowsers.triggeredBy(packageJS in Compile)
  )
