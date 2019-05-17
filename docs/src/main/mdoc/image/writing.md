# Writing to a File

The @scaladoc[Image DSL](doodle.image.index) can write a file as well as displaying on screen. The file formats you can write depend on the backend you're using:

* the Java2D backend can write PNG, GIF, and JPEG;
* the SVG backend running on the JVM can write SVG; and
* the SVG backend running in the web browser cannot write files.

Most people will use the Java2D backend and the examples below show this.


## Imports

In addition to the usual imports of

```scala mdoc
import doodle.image._
import doodle.image.syntax._
import doodle.core._
```

to write to a file you'll need

```scala mdoc
import doodle.java2d._
import doodle.effect.Writer._
```

The first import pulls in the Java2D backend. The second import makes available the standard formats supported by Doodle.


## Writing to a File

To write to a file, call the `write` method, giving the format as a type parameter and the file name as a normal parameter. For example, to write a PNG to the file called `"circle.png"`

```scala mdoc
val image = Image.circle(100)
  .strokeWidth(10.0)
  .fillColor(Color.crimson)
  
image.write[Png]("circle.png")
```

Instead of using `Png` you could use `Gif` or `Jpg` to specify those file formats. You'd probably want to change the file name as well if you change the format. That's it! Your masterpiece is now ready to share with the world.
