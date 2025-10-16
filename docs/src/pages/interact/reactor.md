# Reactor

Just like Doodle provides `Image` as a simpler alternative to `Picture`, it provides @:api(doodle.reactor.Reactor) as a simple alternative for creating reactive animations. However, unlike `Image` and `Picture`, there is no simple translation from a `Reactor` to the more powerful tools described later. We cover `Reactor` here, and in the following sections we describe the more powerful tools that are available.

To use `Reactor`, you should add the following imports in addition to the standard Doodle imports and the imports required by the backend you've chosen.

```scala mdoc:compile-only
import doodle.reactor.*
import doodle.reactor.syntax.all.*
```

## Basics

Let's start with an example of a `Reactor`. Move your mouse around and you'll see the circle follows the mouse. 

@:doodle("following", "ReactorFollowing.go")

It's not the most exciting example, but the code is easy to understand.

```scala mdoc:silent
import doodle.core.*
import doodle.image.*
import doodle.reactor.*

final case class State(point: Point)

val reactor =
  Reactor
    .init(State(Point.zero))
    .withOnMouseMove((pt, state) => state.copy(point = pt))
    .withRender(state =>
      Image
        .circle(20)
        .strokeColor(Color.white)
        .strokeWidth(3.0)
        .fillColor(Color.hotPink)
        .at(state.point)
    )
```
