package object docs {
  import java.io.File
  import doodle.image.Image
  import doodle.effect.Writer._
  import doodle.java2d._

  implicit class ImageSaveSyntax(image: Image) {
    import doodle.image.syntax._
    def save(filename: String): Unit = {
      val dir = new File("docs/src/main/mdoc/")
      val file = new File(dir, filename)
      image.write[Png](file)
    }
  }

  implicit class PictureSaveSyntax(picture: Picture[Unit]) {
    import doodle.syntax._
    def save(filename: String): Unit = {
      val dir = new File("docs/src/main/mdoc/")
      val file = new File(dir, filename)
      picture.write[Png](file)
    }
  }
}
