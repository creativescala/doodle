# Changelog

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
