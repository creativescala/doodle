import sbt._
import org.scalajs.sbtplugin.ScalaJSPlugin
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import org.portablescala.sbtplatformdeps.PlatformDepsPlugin.autoImport._

object Dependencies {
  // Library Versions
  val catsVersion = "2.10.0"
  val catsEffectVersion = "3.5.2"
  val fs2Version = "3.9.2"

  val scalatagsVersion = "0.12.0"

  val batikVersion = "1.17"

  val scalaCheckVersion = "1.15.4"
  val munitVersion = "0.7.29"
  val munitCatsEffectVersion = "1.0.7"

  // Libraries
  val catsEffect =
    Def.setting("org.typelevel" %%% "cats-effect" % catsEffectVersion)
  val catsCore = Def.setting("org.typelevel" %%% "cats-core" % catsVersion)
  val catsFree = Def.setting("org.typelevel" %%% "cats-free" % catsVersion)
  val fs2 = Def.setting("co.fs2" %%% "fs2-core" % fs2Version)

  val scalatags = Def.setting("com.lihaoyi" %%% "scalatags" % scalatagsVersion)

  val batik =
    Def.setting("org.apache.xmlgraphics" % "batik-transcoder" % batikVersion)

  val munit = Def.setting("org.scalameta" %%% "munit" % munitVersion % "test")
  val munitScalaCheck =
    Def.setting("org.scalameta" %%% "munit-scalacheck" % munitVersion % "test")
  val munitCatsEffect =
    Def.setting(
      "org.typelevel" %%% "munit-cats-effect-3" % munitCatsEffectVersion % Test
    )
}
