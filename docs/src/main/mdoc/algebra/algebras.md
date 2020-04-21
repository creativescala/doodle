# Available Algebras

This section gives a quick overview of the main algebras in Doodle. It is not intended to replicate the details you can find in the @scaladoc[ScalaDoc](doodle.algebra.index). Read that for more details. All algebras have corresponding syntax that adds the methods they define to `Picture`.

- @scaladoc[Algebra](doodle.algebra.Algebra) is a marker trait that allow other algebras extend. It provides no useful functionality of its own.

- @scaladoc[Bitmap](doodle.algebra.Bitmap) is a convenience to load a bitmap image as a `Picture`. This is easy to use but not very flexible, and it does not have any error handling. For more complex cases use the `ImageIO` in the Java standard library to load a `BufferedImage` and then use the `toPicture` method to convert to a `Picture`.

- @scaladoc[Blend](doodle.algebra.Blend) specifies blending modes.

- @scaladoc[Debug](doodle.algebra.Debug) renders a picture's bounding box and origin, which is useful for debugging layout issues.

- @scaladoc[Layout](doodle.algebra.Layout) provides basic layout operations.

- @scaladoc[Path](doodle.algebra.Path) converts a @scaladoc[ClosedPath](doodle.core.ClosedPath) or an @scaladoc[OpenPath](doodle.core.OpenPath) into a `Picture`.

- @scaladoc[Shape](doodle.algebra.Shape) is a convenience for drawing some simple shapes, such as circles and squares.

- @scaladoc[Size](doodle.algebra.Size) gets the size of a `Picture`, which can be used for complex layout, for example.

- @scaladoc[Style](doodle.algebra.Style) allows changing the stroke, fill, and other characterisitics of a `Picture`.

- @scaladoc[Text](doodle.algebra.Text) adds text to a `Picture`.

- @scaladoc[ToPicture](doodle.algebra.ToPicture) converts other types to a `Picture`.

- @scaladoc[Transform](doodle.algebra.Transform) applies geometrics transformations, such as rotations and scaling, to a `Picture`.

