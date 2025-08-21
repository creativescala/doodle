# Working With Bitmaps

The Canvas backend provides methods to load bitmap images in various formats provided by the web platform (i.e. the browser) and convert them to a Doodle `Picture`.


## Loading Bitmaps

The Canvas backend supports loading images from URLs using the @:api(doodle.algebra.LoadBitmap) algebra. Images can be loaded as either a [`HTMLImageElement`][html-image-element] or an [`ImageBitmap`][image-bitmap].
A `HTMLImageElement` is the standard DOM image element, while `ImageBitmap` is optimized for faster rendering.

Both instances of the @:api(doodle.algebra.LoadBitmap) algebra have the same input type (`String`) which specifies the URL from which to load the image. To distinguish between these instances you need to specify the output type when calling `loadBitmap`.

```scala
import cats.effect.unsafe.implicits.global
import doodle.canvas.{*, given}
import doodle.syntax.all.*
import org.scalajs.dom

// Image as a Base64 encoded URL
val url =
  "data:image/png:base64,..."

// Load as HTMLImageElement
val htmlImage = url.loadBitmap[dom.HTMLImageElement]

// Load as ImageBitmap (GPU-optimized)
val imageBitmap = url.loadBitmap[dom.ImageBitmap]
```

### Live Example

The following example loads an image and displays it:

@:doodle("canvas-load-bitmap", "CanvasLoadBitmapExamples.demo")


## Converting Bitmaps to Picture

The Canvas backend has implementations of the @:api(doodle.algebra.ToPicture) algebra for `HTMLImageElement` and `ImageBitmap`, which allows them to be converted to a `Picture`. Take, for example, the image below.

![A hot air balloon](hot-air-balloon.png)

With the following code we can find the `HTMLImageElement` in the DOM and turn it into a `Picture`.

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

The result of this is shown below.

@:doodle("to-html-image-picture", "CanvasToPictureExamples.toHtmlImagePicture")


[html-image-element]: https://developer.mozilla.org/en-US/docs/Web/API/HTMLImageElement
[image-bitmap]: https://developer.mozilla.org/en-US/docs/Web/API/ImageBitmap
