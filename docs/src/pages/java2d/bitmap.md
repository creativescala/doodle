# Bitmaps in Java2D

The Java2D backend supports loading bitmap images and converting them to pictures, and converting pictures to bitmaps.


## Loading Bitmaps

Loading bitmap images from disk and converting them to pictures works as described in the [section on bitmaps](../pictures/bitmap.md). Specifically, bitmaps can be loaded as instances of `BufferedImage` from a `File` or a `Path`. The implementation uses `ImageIO`, and hence supports all the bitmap formats that `ImageIO` supports. The example below shows how this works.

```scala mdoc:compile-only
import doodle.syntax.all.*
import doodle.java2d.{*, given}

import java.io.File
import java.nio.file.Paths

File("/path/to/bitmap.png").loadBitmap
Paths.get("/path/to/bitmap.png").loadBitmap
```


## Converting Bitmaps to Pictures

A `BufferedImage` can also be converted to a `Picture` using the `toPicture` method. Instances of @:api(doodle.core.Base64) can also be converted to a `Picture` in the same way.


## Converting Pictures to Bitmaps

A `Picture` can be converted to a bitmap either as a `BufferedImage` or as @:api(doodle.core.Base64) data.

Call the `bufferedImage` method to convert a `Picture` to a `BufferedImage`.


```scala mdoc:silent
import cats.effect.unsafe.implicits.global
import doodle.syntax.all.*
import doodle.java2d.{*, given}

val bitmap = Picture.circle(200).bufferedImage()
```

The following variants of `bufferedImage` are available:

- `bufferedImage()`, shown above, use a default `Frame`;
- `bufferedImage(frame)` allows you to specify a `Frame`; and
- `bufferedImageToIO()` and `bufferedImageToIO(frame)` work like `bufferedImage` but return an `IO[BufferedImage]` instead of a `BufferedImage`.


Use the `base64` method to convert a `Picture` to @:api(doodle.core.Base64) encoded data. You will need to specify a @:api(doodle.core.format.Format) as a type parameter, which determines the bitmap format that is used. The example below shows encoding a `Picture` as a PNG.

```scala mdoc:reset:silent
import cats.effect.unsafe.implicits.global
import doodle.core.format.Png
import doodle.syntax.all.*
import doodle.java2d.{*, given}

val base64 = Picture.circle(200).base64[Png]()
```

The following method variants are available:

- `base64[Format](frame)` to specify a `Frame` to use instead of the default; and
- `base64ToIO[Format]()` and `base64[Format](frame)` to return a `IO[Base64[Format]]`.
