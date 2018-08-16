import sbt._

object Dependencies {
  // Library Versions
  val catsVersion       = "1.2.0"
  val catsEffectVersion = "0.10.1"

  val monixVersion = "3.0.0-RC1"

  val miniTestVersion   = "2.1.1"
  val scalaCheckVersion = "1.14.0"


  // Libraries
  val catsEffect = "org.typelevel" %% "cats-effect" % catsEffectVersion
  val catsCore   = "org.typelevel" %% "cats-core"   % catsVersion

  val miniTest     = "io.monix" %% "minitest"      % miniTestVersion % "test"
  val miniTestLaws = "io.monix" %% "minitest-laws" % miniTestVersion % "test"

  val monix = "io.monix" %% "monix" % monixVersion
}
