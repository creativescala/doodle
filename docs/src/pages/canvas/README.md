# Doodle Canvas

The Canvas backend supports the [HTML Canvas][html-canvas]. It currently supports the following algebras: `Debug`, `Layout`, `Path`, `Shape`, `Size`, `Style`, and `Transform`.


## Usage

Firstly, bring everything into scope

```scala 
import doodle.canvas.{*, given}
```

Construct a `Frame` with the `id` of the DOM element where you'd like the picture drawn.

For example, if you have the following element in your HTML

``` html
<div id="canvas-root"></div>
```

then you can create a `Frame` referring to it with

``` scala mdoc:silent
val frame = Frame("canvas-root")
```

Now suppose you have a picture called `thePicture`. You can draw it using the frame you just created like so

``` scala
thePicture.drawWithFrame(frame)
```

The rendered picture will appear where the element is positioned on your web page.


## Frame

The `Frame` must specify the `id` of the element where the drawing should be placed.
Additional options include the background color and the frame size. The frame size can either be determined by the size of the picture or a fixed size. The first case is the default, and includes a 20 pixel border. You can change this by calling `withSizedToPicture`. The picture will be drawn centered on the drawing area. A fixed size frame, specified with `withSize`, has the given size and the origin is set to the center of the drawing area. This means that pictures that are not centered a the origin will not be centered on the screen.



[html-canvas]: https://developer.mozilla.org/en-US/docs/Web/HTML/Element/canvas
