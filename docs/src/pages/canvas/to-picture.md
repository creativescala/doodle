# Loading Images

The Canvas backend has implementations of the @:api(doodle.algebra.ToPicture) algbra that allow converting common HTML image types into a `Picture`. Take, for example, the image below.

![A hot air balloon](hot-air-balloon.png)

With the following code 
```scala
import cats.effect.unsafe.implicits.global
import doodle.canvas.{*, given}
import doodle.syntax.all.*
import org.scalajs.dom

val img =
  dom.document.querySelector("img").asInstanceOf[dom.HTMLImageElement]
val picture = img.toPicture[Algebra]

picture.drawWithFrame(Frame(id))
```

we produce the image shown below

@:doodle("to-html-image-picture", "CanvasToPictureExamples.toHtmlImagePicture")
