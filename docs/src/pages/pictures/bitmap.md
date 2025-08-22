# Bitmaps

Doodle provides methods to load bitmaps and convert them into pictures. This support is very dependent on the backend, so consult the documentation for the backends you use to see the specifics of what is available.


## Loading Bitmaps

The functionality to load bitmaps is provided by the @:api(doodle.algebra.LoadBitmap) algebra. It is parameterized by two types:

1. the `Specifier` type, which is used to specify where the bitmap is found; and
2. the `Bitmap` type, which is the type of the bitmap that will be loaded.

For example, on the JVM the specifier can be either a `File` or a `Path` and the bitmap type is a `BufferedImage`. However, for the Canvas backend the specifier is a `String` URL while the bitmap type can be a `HTMLImageElement` or a `ImageBitmap`.

The idiomatic way to load a bitmap is by importing `doodle.syntax.all.*` and calling `loadBitmap` on a method. Here's an example for the Java2d backend, using a `File`.

```scala mdoc:compile-only
import doodle.syntax.all.*
import doodle.java2d.{*, given}

import java.io.File

File("/path/to/bitmap.png").loadBitmap
```

The result is an `IO` containing the bitmap type, which in this case is `BufferedImage`.


## Converting Bitmaps to Pictures

A bitmap can be converted to a `Picture` by calling the `toPicture` method. To load and convert a bitmap in one call, use the `loadToPicture`. The example below shows both methods in use.

```scala mdoc:compile-only
import doodle.syntax.all.*
import doodle.java2d.{*, given}

import java.io.File
// Convert to a Picture in two steps
File("/path/to/bitmap.png").loadBitmap.toPicture

// Load and convert to Picture in one step
File("/path/to/bitmap.png").loadToPicture
```


This functionality is provided by instances of the @:api(doodle.algebra.ToPicture) algebra, which provides a generic way to convert types to `Picture`.


## Implementation

This functionality is backend specific and is not available on `Image`.
