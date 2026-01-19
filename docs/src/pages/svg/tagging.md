# Tagging Pictures

The SVG backend allows you to add [ScalaTags][scalatags] tags and attributes into SVG output.
For example, we can add a hyperlink to an element within a SVG drawing using the
`link` method. Here's an example. Notice that we import
`doodle.svg.syntax.all.*` to get the `link` extension method.

```scala
import doodle.core.*
import doodle.svg.*
import doodle.svg.syntax.all.*
import doodle.syntax.all.*

Picture
  .regularPolygon(7, 15)
  .strokeWidth(5.0)
  .beside(Picture.text("Creative Scala").noStroke.fillColor(Color.black))
  .link("https://creativescala.org/")
```

This produces the output shown below.

@:doodle("draw-link", "SvgTaggedExamples.drawLink")

You can add arbitrary tags using the `tagged` method. The supplied tags will be
wrapped around the SVG drawing on which they are called. Note that should only
use tags in the SVG namespace. Mixing HTML and SVG tags will likely result in a
drawing that does not render. 

We can also add an attribute using the `attribute` method. Here's an example.

```scala
import doodle.core.*
import doodle.svg.*
import doodle.svg.syntax.all.*
import doodle.syntax.all.*
import scalatags.JsDom.*
import scalatags.JsDom.all.*
import org.scalajs.dom

Picture
  .regularPolygon(7, 15)
  .strokeWidth(5.0)
  .beside(Picture.text("Click me!").noStroke.fillColor(Color.black))
  .attribute(style := "cursor: pointer;")
  .attribute(onclick := { () =>
    dom.window.alert("You clicked me!")
  })
```

This produces the output shown below. Notice that the pointer changes when over
the drawing, and when you click it you should see an alert box pop up.

@:doodle("draw-attribute", "SvgAttributedExamples.drawWithAttribute")

This functionality currently does not allow you add tags that do not surround a SVG picture created via Doodle. If you would
benefit from removing these limitations, please join our Discord and mention
this. 

Note this only works on SVG, and attempting to use it with a
different backend will fail to compile.

[scalatags]: https://github.com/com-lihaoyi/scalatags
