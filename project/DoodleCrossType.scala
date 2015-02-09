import sbt._
import org.scalajs.sbtplugin.cross.CrossType

/**
 * ScalaJS CrossType that removed the project name from
 * the default cross-build directory structure,
 * creating the following layout:
 *
 * ```
 *  - root/
 *     - shared / src / {main,test} / scala
 *     - jvm    / src / {main,test} / scala
 *     - js     / src / {main,test} / scala
 * ```
 */
object DoodleCrossType extends CrossType {
  def projectDir(crossBase: File, projectType: String): File =
    (crossBase / ".." / projectType).getCanonicalFile

  def sharedSrcDir(projectBase: File, conf: String): Option[File] =
    Some((projectBase / ".." / "shared" / "src" / conf / "scala").getCanonicalFile)
}
