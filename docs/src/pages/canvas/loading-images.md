# Loading Images

## ToPicture conversion

The Canvas backend has implementations of the @:api(doodle.algebra.ToPicture) algebra that allow converting common HTML image types into a `Picture`. Take, for example, the image below.

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

## LoadBitmap images

The Canvas backend supports loading images from URLs using the @:api(doodle.algebra.LoadBitmap) algebra. Images can be loaded as either `HTMLImageElement` or `ImageBitmap`.

### Basic Usage

Since Canvas has two `LoadBitmap` instances with the same input type (String), you need to specify the output type:

```scala
import cats.effect.unsafe.implicits.global
import doodle.canvas.{*, given}
import doodle.syntax.all.*
import org.scalajs.dom

// Load as HTMLImageElement
val htmlImage = url.loadBitmap[dom.HTMLImageElement]

// Load as ImageBitmap (GPU-optimized)
val imageBitmap = url.loadBitmap[dom.ImageBitmap]
```

The Disambiguation Patterns:
- `LoadBitmap[String, HTMLImageElement]` - Standard DOM image element
- `LoadBitmap[String, ImageBitmap]` - GPU-optimized bitmap for processing

### Live Example

The following example loads an image and displays it:

@:doodle("canvas-load-bitmap", "CanvasLoadBitmapExamples.demo")
