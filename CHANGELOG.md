# Changelog

## 0.9.14

- `Picture` has a `Monad` instance if the underlying type `F` does.

- Refactor base64 encoding support into a separate type class, and augment the syntax with an optional frame.

- Fix `Java2dWriter` use of 0 size `BufferedImage`

- Fix error in calculating bounding box if the picture uses `at` layout. This will prevent the issue where the frame is too large or small for the enclosed picture.

- Add `Debug` algebra, which draws bounding box and origin for the given picture.

- Miscellaneous small improvements.


## 0.9.13 21-Nov-2019

- Add text rendering via the `Text` algebra. Currently only implemented for the Java2D back-end.

- Add syntax for `Size` and `Transform` algebras

- Add derived methods for some common types of shapes to the `Path` algebra. Add syntax for these.


## 0.9.12 16-Nov-2019

- Doodle has been completely rewritten in terms of tagless final algebras. This gives extensibility needed to support diffferent features on different platforms.

- 0.9.12 add the ability to load bitmap images on the java2d backend (the `read` methods on the `Bitmap` algebra.) It's likely this API will be reworked in the future. This is very basic support for this feature.

- 0.9.11 added mouse movement events to reactors.


--- Period when I wasn't recording changes ---


## 0.6.5

Fix most SVG text rendering issues. 
- The layout is passable but not great. This will be improved with time.
- Java2D does not support fills for text, to the best of my knowledge, while SVG does. This can lead to different rendering on the two platforms, and we need to think about how we will handle this.

Add some more predefined paths
- `Image.star`
- `Image.regularPolygon`

Improvements to `Random`
- `Random.int(lower,upper)` to generate an `Int` in a given range.
- `Random.discrete((atom, p), ...)` creates a discrete distribution that chooses the atom with the given probability
- Refactoring of `Random` code that avoids strange REPL interactions.

More examples


## 0.6.4

Save to SVG
- the format is `Svg`
  E.g. `myImage.save[Svg]("filename.svg")`

Save to PDF and SVG
- use `PdfAndSvg` as the format, and specify only the base name of the file.
  E.g. `myImage.save[PdfAndSvg]("filename")` will produce `filename.pdf` and `filename.svg`
  
Reduce the size of PDF files by enabling compression


## Earlier

These changes are lost in the mists of time
