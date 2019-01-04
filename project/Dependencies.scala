import sbt._

object Dependencies {
  // Library Versions
  val catsVersion       = "1.5.0"
  val catsEffectVersion = "1.1.0"

  val magnoliaVersion = "0.10.0"

  val monixVersion = "3.0.0-RC2"

  val miniTestVersion   = "2.2.2"
  val scalaCheckVersion = "1.14.0"


  // Libraries
  val catsEffect = "org.typelevel" %% "cats-effect" % catsEffectVersion
  val catsCore   = "org.typelevel" %% "cats-core"   % catsVersion
  val catsFree   = "org.typelevel" %% "cats-free"   % catsVersion

  val miniTest     = "io.monix" %% "minitest"      % miniTestVersion % "test"
  val miniTestLaws = "io.monix" %% "minitest-laws" % miniTestVersion % "test"

  val monix = "io.monix" %% "monix" % monixVersion

  val magnolia = "com.propensive" %% "magnolia" % "0.10.0"
}
