# Doodle SVG

Doodle SVG draws Doodle pictures to SVG, both in the browser using [Scala JS](https://scala-js.org/) and to files on the JVM.

## Usage

Firstly, bring everything into scope

```scala 
import doodle.svg._
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

On the JVM you can't draw SVG to the screen. Use the `java2d` backend for that instead. However you can write SVG output in the usual way.


## Examples

The source for these examples is [in the repository](https://github.com/creativescala/doodle-svg/tree/main/examples/src/main/scala).

### Concentric Circles
@:doodle("concentric-circles", "ConcentricCircles.draw")

### Text Positioning
@:doodle("text-positioning", "TextPositioning.draw")

### Doodle Logo
@:doodle("doodle-logo", "DoodleLogo.draw")

### Pulsing Circle
@:doodle("pulsing-circle", "PulsingCircle.draw")

### Parametric Spiral
@:doodle("parametric-spiral", "ParametricSpiral.draw")
