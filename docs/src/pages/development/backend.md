# Implementing a Backend

This is a quick guide to implementing a backend. It assume familiarity with the major [concepts](../concepts/README.md) of Doodle.


## Code Organization

The majority of the backend code should be in the package `doodle.<backend>.algebra`, with files in the corresponding directory. Here you'll implement all the algebras for your backend.

In `doodle.<backend>.effect` you should implement your `Frame`, `Canvas`, `Renderer`, and any other effect types.


## Drawing Type

Deciding on the `Drawing` type is the first step in implementing a backend. It must have the shape `F[A]`, where `A` is the value produced when drawing the drawing. You probably don't want to call it `Drawing`, because that name is used in the generic algebras you'll be extending. `<Backend>Drawing` is a good choice. This goes in the `algebra` package.


## Top-level Definitions

For ease of use, users should just be able to write

```scala
import doodle.<backend>.{*, given}
```

to use the backend.

In a file (usually called `package.scala`, though we don't use package objects any more in Scala 3) you should define all the types and values needed to use the backend. Something like the following. Make sure you add all the necessary constructors to the `Picture` object.

```scala
package doodle.<backend>

import doodle.algebra.*
import doodle.effect.Renderer

type Algebra = doodle.<backend>.algebra.<Backend>Algebra
type Canvas = doodle.<backend>.effect.Canvas
type Drawing[A] =
  doodle.algebra.generic.Finalized[doodle.<backend>.algebra.<Backend>Drawing, A]

type Frame = doodle.<backend>.effect.Frame
val Frame = doodle.<backend>.effect.Frame

given Renderer[Algebra, Frame, Canvas] = doodle.<backend>.effect.<Backend>Renderer

type Picture[A] = doodle.algebra.Picture[Algebra, A]
object Picture extends BaseConstructor, ShapeConstructor {

  type Algebra = doodle.<backend>.Algebra
  type Drawing[A] = doodle.<backend>.Drawing[A]
}
```
