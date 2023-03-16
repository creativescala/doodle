import sbt._
import org.scalajs.sbtplugin.ScalaJSPlugin
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import org.portablescala.sbtplatformdeps.PlatformDepsPlugin.autoImport._

object Dependencies {
  // Library Versions
  val catsVersion = "2.9.0"
  val catsEffectVersion = "3.4.8"
  val fs2Version = "3.6.1"

  val batikVersion = "1.16"

  val miniTestVersion = "2.9.6"
  val scalaCheckVersion = "1.15.4"
  val munitVersion = "0.7.29"

  // Libraries
  val catsEffect =
    Def.setting("org.typelevel" %%% "cats-effect" % catsEffectVersion)
  val catsCore = Def.setting("org.typelevel" %%% "cats-core" % catsVersion)
  val catsFree = Def.setting("org.typelevel" %%% "cats-free" % catsVersion)
  val fs2 = Def.setting("co.fs2" %%% "fs2-core" % fs2Version)

  val batik =
    Def.setting("org.apache.xmlgraphics" % "batik-transcoder" % batikVersion)

  val miniTest =
    Def.setting("io.monix" %%% "minitest" % miniTestVersion % "test")
  val miniTestLaws =
    Def.setting("io.monix" %%% "minitest-laws" % miniTestVersion % "test")
  val munit = Def.setting("org.scalameta" %%% "munit" % munitVersion % "test")
  val munitScalaCheck =
    Def.setting("org.scalameta" %%% "munit-scalacheck" % munitVersion % "test")
}
