# Reactor

Just like Doodle provides `Image` as a simpler alternative to `Picture`, it provides @:api(doodle.reactor.Reactor) as a simple alternative for creating reactive animations. However, unlike `Image` and `Picture`, there is no simple translation from a `Reactor` to the more powerful tools described later. We cover `Reactor` here, and in the following sections we describe the more powerful tools that are available.

To use `Reactor`, you should add the following imports in addition to the standard Doodle imports and the imports required by the backend you've chosen.

```scala mdoc:compile-only
import doodle.reactor.*
import doodle.reactor.syntax.all.*
```

## Basics

Let's start with an example of a `Reactor`. Move your mouse around and you'll see the circle follows the mouse. Click and the circle will change color.

@:doodle("following", "ReactorFollowing.go")

It's not the most exciting example, but the code is easy to understand.

```scala mdoc:silent
import doodle.core.*
import doodle.image.*
import doodle.reactor.*
import doodle.reactor.syntax.all.*

final case class State(point: Point, color: Color)

val reactor =
  Reactor
    .init(State(Point.zero))
    .withOnMouseMove { (pt, state) =>
      state.copy(point = pt)
    }
    .withOnMouseClick { (pt, state) =>
      state.copy(color = state.color.spin(15.degrees))
    }
    .withRender(state =>
      Image
        .circle(20)
        .strokeColor(Color.white)
        .strokeWidth(3.0)
        .fillColor(Color.hotPink)
        .at(state.point)
    )
```

A `Reactor` is essentially a finite state machine. It has some internal state, which in this case is simply a `Point` and a `Color` wrapped in the `State` case class. The state can be updated in response to some external events. In the example above we react to mouse movement and mouse clicks.
 We also tell the `Reactor` how to convert the state into an `Image` that can be displayed. In this example we simply use the `Point` to position a circle and fill it with the `Color`.

We can run the `Reactor` above by importing a backend and then calling the `animate` method. In the code below we use the Java2D backend.

```scala mdoc:compile-only
import cats.effect.unsafe.implicits.global
import doodle.java2d.*

reactor.animate()
```


## Details

Everything is a `Reactor` revolves around the internal state it maintains. We specify updates to that state in response to events, and we convert that state to an `Image` to draw it. We create a `Reactor` by calling the @:api(doodle.reactor.Reactor$#init) method, passing in the initial value for the state.

To respond to events we add callbacks. The `withOnMouseClick` and `withOnMouseMove` allow us to respond to mouse events. We can also respond to the passage of time, using `withOnTick`. The function passed to `withOnTick` is called at a regular interval. This interval defaults to once every 100 milliseconds, but can be adjusted by called `withTickRate`.

To have any visible output we pass a `State => Image` function to `withRender`. Finally, we can determine when to stop the `Reactor` by passing a `State => Boolean` function to `withStop`.

