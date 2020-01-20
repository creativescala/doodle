import sbt._
import org.scalajs.sbtplugin.ScalaJSPlugin
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import org.portablescala.sbtplatformdeps.PlatformDepsPlugin.autoImport._


object Dependencies {
  // Library Versions
  val catsVersion       = "2.1.0"
  val catsEffectVersion = "2.0.0"

  val magnoliaVersion = "0.10.0"

  val monixVersion = "3.1.0"

  val miniTestVersion   = "2.7.0"
  val scalaCheckVersion = "1.14.0"


  // Libraries
  val catsEffect = Def.setting("org.typelevel" %%% "cats-effect" % catsEffectVersion)
  val catsCore   = Def.setting("org.typelevel" %%% "cats-core"   % catsVersion)
  val catsFree   = Def.setting("org.typelevel" %%% "cats-free"   % catsVersion)

  val miniTest     = Def.setting("io.monix" %%% "minitest"      % miniTestVersion % "test")
  val miniTestLaws = Def.setting("io.monix" %%% "minitest-laws" % miniTestVersion % "test")

  val monix = Def.setting("io.monix" %%% "monix" % monixVersion)

  val magnolia = Def.setting("com.propensive" %%% "magnolia" % "0.12.6")

  val scalaTags = Def.setting("com.lihaoyi" %%% "scalatags" % "0.8.4")
}
