# Images in SVG

```scala mdoc:invisible
import cats.effect.unsafe.implicits.global
import doodle.core.*
import doodle.svg.{*, given}
import doodle.svg.algebra.{SvgImageRef, SvgLoadBitmap}
import doodle.syntax.all.*
```

The SVG backend provides support for bitmap and SVG images through `<image>` elements. Unlike raster-based backends, SVG doesn't load actual pixel data. Instead, it creates image references that the browser loads and renders. Per the [documentation](https://developer.mozilla.org/en-US/docs/Web/SVG/Reference/Element/image), it must support at least JPEG, PNG, and SVG formats.


## How SVG Handles Bitmaps

SVG handles bitmaps differently from other backends:

- **No pixel manipulation**: SVG creates `<image>` elements with `href` attributes pointing to image sources.
- **Browser-handled loading**: the browser handles fetching images and respecting CORS policies.
- **Vector context**: bitmaps are embedded in a vector graphics context and can have SVG transformations and filters applied.


## Loading Bitmap References

In the SVG backend, "loading" a bitmap creates an `SvgImageRef` which stores the image URL and optional dimensions. The specifier type is `String` (representing a URL or data URI).

```scala mdoc:compile-only
// Load from URL
val imageRef = "https://example.com/image.png".loadBitmap[SvgImageRef]

// Load from data URI
val dataUri = "data:image/png;base64,iVBORw0KGgoAAAANS..."
val dataRef = dataUri.loadBitmap[SvgImageRef]

// Load from relative path
val localRef = "images/logo.svg".loadBitmap[SvgImageRef]
```

Note* that `loadBitmap` returns an `IO[SvgImageRef]`. Use `.unsafeRunSync()` to extract the value in examples, or handle the `IO` properly in production code.


## Specifying Dimensions

You can optionally specify dimensions when creating image references:

```scala mdoc:compile-only
// With both width and height
val withDims = SvgLoadBitmap.withDimensions("image.jpg", width = 200, height = 150)

// With width only (height auto-calculated by browser)
val withWidth = SvgLoadBitmap.withWidth("image.png", width = 300)

// With height only (width auto-calculated by browser)
val withHeight = SvgLoadBitmap.withHeight("image.gif", height = 250)
```

If dimensions are not specified, the SVG backend uses a default size of 100x100 pixels for bounding box calculations.


## Converting to Pictures

An `SvgImageRef` can be converted to a `Picture` using the `toPicture` method:

```scala mdoc:compile-only
val imageRefIO = "photo.jpg".loadBitmap[SvgImageRef]
val imageRef = imageRefIO.unsafeRunSync()
val picture = imageRef.toPicture

// Or load and convert in one step
val directPictureIO = "photo.jpg".loadBitmap[SvgImageRef].toPicture
val directPicture = directPictureIO.unsafeRunSync()
```


## Composing with Vector Graphics

Since bitmap references become regular `Pictures`, they can be composed with vector graphics:

```scala mdoc:compile-only
val logoIO = for {
  imageRef <- "logo.png".loadBitmap[SvgImageRef]
  logo = imageRef.toPicture
} yield logo
  .on(circle(150).fillColor(Color.lightBlue))
  .beside(text("Welcome").fillColor(Color.black))

val composition = logoIO.unsafeRunSync()
```


## SVG Filters on Bitmaps

SVG filters can be applied to bitmap images just like any other `Picture`:

```scala mdoc:compile-only
val filteredIO = for {
  imageRef <- "photo.jpg".loadBitmap[SvgImageRef]
  image = imageRef.toPicture
} yield image
  .blur(3.0)
  .dropShadow(4, 4, 2, Color.black.alpha(0.5.normalized))

val filtered = filteredIO.unsafeRunSync()
```


## Complete Example

This example demonstrates loading an image, applying transformations, and composing with vector graphics:

```scala mdoc:compile-only
val program = for {
  // Load the image reference
  imageRef <- "https://example.com/logo.png".loadBitmap[SvgImageRef]

  // Convert to Picture
  logo = imageRef.toPicture

  // Create composition
  composition = logo
    .scale(0.75, 0.75)
    .on(square(200).fillColor(Color.lightGray))
    .above(text("Company Name").fillColor(Color.darkGray))

} yield composition

// In production, handle IO properly
// For examples, we can use unsafeRunSync
val result = program.unsafeRunSync()
```


## Important Considerations

### CORS Policies

When loading images from external domains, be aware of CORS (Cross-Origin Resource Sharing) policies.

### No Pixel Access

Unlike Java2D or Canvas backends, SVG cannot:

- Access individual pixels.
- Convert Pictures to bitmap data.
- Apply pixel-level manipulations.

For these operations, use a raster-based backend.
