import sbt._
import org.scalajs.sbtplugin.cross.CrossType

object DoodleCrossType extends CrossType {
  def projectDir(crossBase: File, projectType: String): File = {
    println(s"projectDir ${crossBase} ${projectType}")
    ( crossBase / ".." / projectType ).getCanonicalFile
  }

  def sharedSrcDir(projectBase: File, conf: String): Option[File] = {
    println(s"sharedSrcDir ${projectBase} ${conf}")
    Some( (projectBase / ".." / "shared" / "src" / conf / "scala").getCanonicalFile )
  }
}
