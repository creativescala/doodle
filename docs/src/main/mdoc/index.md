# Doodle

@@@ index

- [Core](core/index.md)
- [Image](image/index.md)

@@@

Doodle is a library for 2D graphics. It runs in both the JVM, rendering via [Java2D][java2d], and the web browser using [SVG][svg].

Doodle provides a simple to use DSL, called @ref[Image](image/index.md). This DSL is in turn built on tagless final algebras. The tagless final algebras allow Doodle to support features that are not available across all backends (addressing the so-called [expression problem][expression-problem]). Doodle also provides libraries for animation and other utilties.

## Getting Started

To use Doodle, add to your `build.sbt`

```scala
libraryDependencies += "org.creativescala" %% "doodle" % "@VERSION@"
```

## ScalaDoc

ScalaDoc is @scaladoc[here](doodle.index)

## Quick Example

The following example creates a Chessboard, by first creating a 2x2 board, then a 4x4 board from the 2x2 board, and finally the complete 8x8 board from the 4x4 board.


```scala mdoc:silent
// The "Image" DSL is the easiest way to create images
import doodle.image._
// Colors and other useful stuff
import doodle.core._

val blackSquare = Image.rectangle(30, 30).fillColor(Color.black)
val redSquare = Image.rectangle(30, 30).fillColor(Color.red)

// A chessboard, broken into steps showing the recursive construction
val twoByTwo =
  (redSquare.beside(blackSquare))
    .above(blackSquare.beside(redSquare))

val fourByFour =
  (twoByTwo.beside(twoByTwo))
    .above(twoByTwo.beside(twoByTwo))

val chessboard =
  (fourByFour.beside(fourByFour))
    .above(fourByFour.beside(fourByFour))
```

To draw these Images call the `draw` method like so


```scala
// Extension methods
import doodle.image.syntax._
// Render to a window using Java2D (must be running in the JVM)
import doodle.java2d._

chessboard.draw()
```


## Library Overview

The main packages of Doodle are:

- @ref[core](core/index.md), which provides common utilities such as @scaladoc[colors](doodle.core.Color), @scaladoc[points](doodle.core.Point), and @scaladoc[parametric curves](doodle.core.Parametric$).
- @ref[image](image/index.md)


[java2d]: https://en.wikipedia.org/wiki/Java_2D
[svg]: https://en.wikipedia.org/wiki/Scalable_Vector_Graphics
[expression-problem]: https://en.wikipedia.org/wiki/Expression_problem
