# Size

## Concept

Establishing the size of a picture is sometimes needed for complex layout, and the @:api(doodle.algebra.Size) algebra provides this.

## Bounding Box

The core method, `boundingBox`, gets the @:api(doodle.core.BoundingBox) of a picture.

```scala mdoc:silent
import doodle.core.*
import doodle.java2d.*
import doodle.syntax.all.*

val circle =
  Picture
    .circle(100)
    .strokeColor(Color.midnightBlue)

val circleBoundingBox: Picture[BoundingBox] =
  circle.boundingBox
```

As the type annotation suggests, this returns a `Picture[BoundingBox]` rather than a `BoundingBox`. 
In other words, the `BoundingBox` will be calculated when the `Picture` is rendered and is not immediately available.
This is necessary because the size may depend on backend-specific calculations, such as the space occupied by text.

To make use of the `BoundingBox` in the creation of a picture we can use the `flatMap` method on `Picture`.

```scala mdoc:silent
import doodle.core.*
import doodle.java2d.*
import doodle.syntax.all.*

val boundingBox =
  circleBoundingBox.flatMap(bb => 
    Picture
      .roundedRectangle(bb.width, bb.height, 3.0)
      .strokeColor(Color.hotpink)
      .strokeWidth(3.0)
  )
  
val picture = circle.on(boundingBox)
```

This produces the picture shown below.

@:image(bounding-box.png)


## Other Methods

The `Size` algebra defines a few utilities derived from the `boundingBox` method:

* `width` gets the height of the enclosing bounding box, as a `Double`;
* `height` gets the height of the enclosing bounding box, as a `Double`; and
* `size` gets the width and height of the enclosing bounding box, as a tuple `(Double, Double)`.


## Implementation

The `Size` algebra supports all the features described above. `Image` does not currently support these operations.
