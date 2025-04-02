import sbt.*
import org.scalajs.sbtplugin.ScalaJSPlugin
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport.*
import org.portablescala.sbtplatformdeps.PlatformDepsPlugin.autoImport.*

object Dependencies {
  // Library Versions
  val catsVersion = "2.13.0"
  val catsEffectVersion = "3.6.0"
  val fs2Version = "3.12.0"

  val scalatagsVersion = "0.13.1"
  val scalajsDomVersion = "2.8.0"

  val batikVersion = "1.18"

  val scalaCheckVersion = "1.15.4"
  val munitVersion = "1.1.0"
  val munitScalacheckVersion = "1.1.0"
  val munitCatsEffectVersion = "2.1.0"

  // Libraries
  val catsEffect =
    Def.setting("org.typelevel" %%% "cats-effect" % catsEffectVersion)
  val catsCore = Def.setting("org.typelevel" %%% "cats-core" % catsVersion)
  val catsFree = Def.setting("org.typelevel" %%% "cats-free" % catsVersion)
  val fs2 = Def.setting("co.fs2" %%% "fs2-core" % fs2Version)

  val scalatags = Def.setting("com.lihaoyi" %%% "scalatags" % scalatagsVersion)
  val scalajsDom =
    Def.setting("org.scala-js" %%% "scalajs-dom" % scalajsDomVersion)

  val batik =
    Def.setting("org.apache.xmlgraphics" % "batik-transcoder" % batikVersion)

  val munit = Def.setting("org.scalameta" %%% "munit" % munitVersion % Test)
  val munitScalaCheck =
    Def.setting(
      "org.scalameta" %%% "munit-scalacheck" % munitScalacheckVersion % Test
    )
  val munitCatsEffect =
    Def.setting(
      "org.typelevel" %%% "munit-cats-effect" % munitCatsEffectVersion % Test
    )
}
