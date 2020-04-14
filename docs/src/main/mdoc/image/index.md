# Image

@@@ index

* [Shapes](shape.md)
* [Paths](path.md)
* [Styling](styling.md)
* [Layout](layout.md)
* [Transforms](transform.md)
* [Writing to a File](writing.md)

@@@

The @scaladoc[Image DSL](doodle.image.index) is the easiest way to create images using Doodle. The tradeoff the Image library makes is that it only support a (large but limited) subset of operations that are supported across all the backends.

## Imports

To use Image you'll need the following imports:

```scala mdoc
import doodle.image._
import doodle.image.syntax._
import doodle.core._
```

## Basic Concepts

Image is based on *composition* and the *interpreter pattern*. 

Composition basically means that we build big Images out of small Images. For example, if we have an Image describing a red square and an Image describing a blue square

```scala mdoc:silent
val redSquare = Image.square(100).fillColor(Color.red)
val blueSquare = Image.square(100).fillColor(Color.blue)
```

we can create an Image describing a red square next to a blue square by combining them together.

```scala mdoc:silent
val combination = redSquare.beside(blueSquare)
```

The interpreter pattern means that we separate describing the Image from rendering it. Writing `Image.square(100)` doesn't draw anything. To draw an image we need to call the `draw()` method. This separation is important for composition; if we were to immediately draw we would lose composition. 


[catmull-rom]: https://en.wikipedia.org/wiki/Centripetal_Catmull%E2%80%93Rom_spline
