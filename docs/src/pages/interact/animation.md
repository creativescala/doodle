# Animations

An animation, in Doodle, in a sequence of `Pictures` produced over time. Concretely, it is a `fs2.Stream[IO, Picture[Alg, A]]`. You can create such a `Stream` yourself, but Doodle provides tools that may be easier to work with:

1. @:api(doodle.interact.animation.Interpolation), which can be used when an animation depends on a range of values between some starting and ending point. For example, an animation of a moving ball can be created from an `Interpolation` between the minimum and maximum position of the ball.


2. @:api(doodle.interact.animation.Transducer), which represents an animation in terms of some internal state, a function to convert that internal state into a `Picture`, and a function to update that internal state into a new state. This can be used for more complex animations that cannot be represented by an `Interpolation`.


## Using Animations

To use animations we'll need the following imports:

```scala mdoc:silent
// The standard Doodle imports
import doodle.core.*
import doodle.syntax.all.*
// Animation specific imports
import doodle.interact.*
import doodle.interact.syntax.all.*
```

To do anything useful we'll also need a backend. Here are the imports for the Java2D backend:

```scala mdoc:silent
import doodle.java2d.*
import cats.effect.unsafe.implicits.global
```


## Example

Here's an example animating a ball moving across the screen. This uses an `Interpolation`, via the `upTo` method, because the position of the ball is a simple function of a value in the range -100.0 to 100.0.

```scala mdoc:silent
val ball =
  -100.0
    .upTo(100.0)
    .map(x =>
      Picture
        .circle(15)
        .fillColor(Color.chartreuse)
        .strokeWidth(3.0)
        .at(x, 0.0)
    )
    .forSteps(100)
    .repeatForever
```

This produces the animation below.

@:doodle("simple", "Simple.ball")
