package object docs {
  import java.io.File
  import doodle.image.Image
  import doodle.image.syntax._
  import doodle.effect.Writer._
  import doodle.java2d._

  implicit class ImageSaveSyntax(image: Image) {
    def save(filename: String): Unit = {
      val dir = new File("docs/src/main/img/")
      if(!dir.exists()) dir.mkdirs()

      val file = new File(dir, filename)
      image.write[Png](file)
    }
  }
}
