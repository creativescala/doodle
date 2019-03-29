# Image

@@@ index

* [Shapes](shape.md)
* [Paths](path.md)
* [Styling](styling.md)
* [Layout](layout.md)
* [Transforms](transform.md)

@@@

The Image DSL is the easiest way to create images using Doodle. The tradeoff the Image library makes is that it only support a (large but limited) subset of operations that are supported across all the backends.

## Imports

To use Image you'll need the following imports:

```scala
import doodle.image._
import doodle.image.syntax._
import doodle.core._
```

## Basic Concepts

Image is based on *composition* and the *interpreter pattern*. Composition basically means that you build big Images out of small Images. For example, if you have an Image describing a red square and an Image describing a blue square

```scala
val redSquare = Image.square(100).fillColor(Color.red)
val blueSquare = Image.square(100).fillColor(Color.blue)
```

you can create an Image describing a red square next to a blue square by combining them together.

```scala
val combination = redSquare.beside(blueSquare)
```



[catmull-rom]: https://en.wikipedia.org/wiki/Centripetal_Catmull%E2%80%93Rom_spline
