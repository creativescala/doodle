# Available Algebras

This section gives a quick overview of the main algebras in Doodle. It is not intended to replicate the details you can find in the @:api(doodle.algebra.index). Read that for more details. All algebras have corresponding syntax that adds the methods they define to `Picture`.

- @:api(doodle.algebra.Algebra) is a marker trait that allow other algebras extend. It provides no useful functionality of its own.

- @:api(doodle.algebra.Bitmap) is a convenience to load a bitmap image as a `Picture`. This is easy to use but not very flexible, and it does not have any error handling. For more complex cases use the `ImageIO` in the Java standard library to load a `BufferedImage` and then use the `toPicture` method to convert to a `Picture`.

- @:api(doodle.algebra.Blend) specifies blending modes.

- @:api(doodle.algebra.Debug) renders a picture's bounding box and origin, which is useful for debugging layout issues.

- @:api(doodle.algebra.Layout) provides basic layout operations.

- @:api(doodle.algebra.Path) converts a @:api(doodle.core.ClosedPath) or an @:api(doodle.core.OpenPath) into a `Picture`.

- @:api(doodle.algebra.Shape) is a convenience for drawing some simple shapes, such as circles and squares.

- @:api(doodle.algebra.Size) gets the size of a `Picture`, which can be used for complex layout, for example.

- @:api(doodle.algebra.Style) allows changing the stroke, fill, and other characterisitics of a `Picture`.

- @:api(doodle.algebra.Text) adds text to a `Picture`.

- @:api(doodle.algebra.ToPicture) converts other types to a `Picture`.

- @:api(doodle.algebra.Transform) applies geometrics transformations, such as rotations and scaling, to a `Picture`.

