# Writing to a File

An `Image` or `Picture` can be written to a file as well as displaying it on screen. The file formats you can write depend on the backend you're using:

* the Java2D backend can write PDF, PNG, GIF, and JPEG; and
* the SVG backend running on the JVM can write SVG; but
* the SVG backend running in the web browser cannot write files.

Most people will use the Java2D backend and the examples below show this.


## Imports

You need the normal imports to do anything with Doodle. Here are the imports for `Picture` and the JVM backend.

```scala mdoc:silent
import doodle.core.*
import doodle.syntax.all.*
import doodle.java2d.*
import cats.effect.unsafe.implicits.global
```

You'll also need an additional import to write output:

```scala mdoc:silent
import doodle.core.format.*
```

This import makes available the standard formats supported by Doodle. 


## Writing to a File

Call the `write` method to write to a file, giving the format as a type parameter and the file name as a `String` parameter. For example, to write a PNG to the file called `"circle.png"`

```scala mdoc:silent
val circle = Picture.circle(100)
  .strokeWidth(10.0)
  .fillColor(Color.crimson)
  
circle.write[Png]("circle.png")
```

Instead of using `Png` you could use `Gif` or `Jpg` to specify those file formats. You'd probably want to change the file name as well if you change the format. That's it! Your masterpiece is now ready to share with the world.
