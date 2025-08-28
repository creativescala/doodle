# SVG Backend

The SVG backend draws Doodle pictures to SVG in the browser using [Scala JS](https://scala-js.org/), and writes them to files on the JVM.

## Usage

Firstly, bring everything into scope

```scala mdoc:silent
import doodle.svg.*
```

Now what you can do depends on whether you are running in the browser or on the JVM.


### Running in the Browser

In the browser you can draw a picture to SVG. To do this, construct a `Frame` with the `id` of the DOM element where you'd like the picture drawn.

For example, if you have the following element in your HTML

``` html
<div id="svg-root"></div>
```

then you can create a `Frame` referring to it with

``` scala mdoc:silent
val frame = Frame("svg-root")
```

Now suppose you have a picture called `thePicture`. You can draw it using the frame you just created like so

``` scala
thePicture.drawWithFrame(frame)
```

The rendered SVG will appear where the element is positioned on your web page.


## Running on the JVM

On the JVM you can't draw SVG to the screen. Use the `java2d` backend for that. However you can write SVG output, as described in [writing to a file](../pictures/writing.md). Here's a simple example.

```scala mdoc:compile-only
import cats.effect.unsafe.implicits.global
import doodle.core.*
import doodle.core.format.*
import doodle.syntax.all.*

val circle = Picture.circle(100)
  .strokeWidth(10.0)
  .fillColor(Color.crimson)
  
circle.write[Svg]("circle.svg")
```
