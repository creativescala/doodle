# Transforms

## Overview

Doodle supports rotating, scaling, translation and reflections with the @:api(doodle.algebra.Transform) algebra. In theory, you can apply any transform to any picture. In practice, some transforms are more useful with some pictures than others. For example, rotating a circle will look the same no matter how many times you rotate it, but rotating a square will look different each time.

```scala mdoc:silent
import doodle.core.*
import doodle.java2d.*
import doodle.syntax.all.*

val hut = 
    Picture
        .triangle(50, 50)
        .fillColor(Color.black)
        .strokeColor(Color.red)
        .above(Picture.rectangle(50, 50).fillColor(Color.blue))

val rotatedHut = hut.rotate(45.degrees)
val scaledHut = hut.scale(3.0, 3.0)
val translatedHut = hut.translate(500, 500)
val verticallyReflectedHut = hut.verticalReflection
```

In the following code, we create a hut (a triangle on top of a rectangle) and apply a rotation, scaling and translation to it. 

### Rotation

@:image(rotation.png) {
    alt = A hut rotated 45 degrees
    title = A hut rotated 45 degrees
}

The `rotate` method rotates a picture by a given angle. The angle is specified in radians, but you can use the `degrees` method to convert degrees to radians.

### Scaling

@:image(scaling.png) {
    alt = A hut scaled by 3
    title = A hut scaled by 3
}

The `scale` method scales a picture by a given factor in the x and y directions.

### Translation

@:image(translation.png) {
    alt = A hut translated to (500, 500)
    title = A hut translated to (500, 500)
}

The `translate` method moves a picture by a given amount in the x and y directions.

### Reflection

@:image(reflection.png) {
    alt = A hut vertically reflected
    title = A hut vertically reflected
}

The `verticalReflection` method reflects a picture vertically. 
[]: # The `horizontalReflection` method reflects a picture horizontally. 

## Implementation

These methods are available on `transform` algebra.













