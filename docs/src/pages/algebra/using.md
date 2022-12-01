# Using Algebras

This section gives recipes for using Doodle's algebras, targetting one backend or working across multiple backends. You should be familiar with the [concepts](/concepts/README.md) behind Doodle to understand everything here.


## Using a Single Backend

Using a single backend should be straightforward:

1. Import the backend you're interested in.
2. Import the syntax.
3. Construct pictures using the methods on the `Picture` object.
4. Compose `Pictures` using the methods provided by the syntax imports.

For example, the following imports are used for the Java2D backend:

```scala
import doodle.java2d._
import doodle.syntax.all._
```

Given these imports we can create pictures. To construct atomic elements we call the constructors on `Picture`.

```scala
val smallCircle = Picture.circle(100)
val largeSquare = Picture.square(200)
```


## Using Backend Specific Features

Use the following recipe to write code using backend specific features (for example, the @:api(doodle.algebra.Bitmap) algebra which is currently only supported by the @:api(doodle.java2d) backend).

The first step is to import the backend and syntax extensions, some Cats implicits we'll need, and the Cats Effect runtime. We'll use `java2d` as our backend.

```scala mdoc:silent
import doodle.java2d._
import doodle.syntax.all._
import cats.implicits._
import cats.effect.unsafe.implicits.global
```

Now we can write code in a style very similar to using `Image`, except the constructors are on the backend's `Picture` object. 

```scala mdoc:silent
val aCircle = Picture.circle(100) // Circle with diameter 100
```

By convention all backends provide a `Picture` companion object, so the above code would work with the SVG backend by simply changing the import from `doodle.java2d._` to `doodle.svg._`

Once we have created a picture we can compose pictures in a straightforward way. For example, to create two red circles beside each other we could write

```scala mdoc:silent
import doodle.core._ // For Color

val redCircle = Picture.circle(100).strokeColor(Color.red)
val twoRedCircles = redCircle.beside(redCircle)
```

To use the `Bitmap` algebra, and assuming the bitmap we refer to exists on disk, we could write

```scala mdoc:silent
val oldGod = Picture.read("old-god.png")
```

@@@note
The bitmap is the [Old God](https://www.deviantart.com/kaiseto/journal/Most-of-my-Pixel-Art-is-now-Creative-Commons-369510391
) created by Kevin Chaloux and released under Creative Commons.
@@@

We could combine this bitmap value with other pictures in the usual way.

```scala mdoc:silent
twoRedCircles.above(oldGod)
```

We can then draw it using the `draw` method, which produces the output shown below.

![Double suns rising over the Old God](suns-old-god.png)


## Creating Cross-Backend Pictures

If we want to use algebras directly and target multiple backends we need to do a little bit more work then when working with a single backend. First we need to decide what algebras we need. Let's say we decide to use @:api(doodle.language.Basic), which is a collection of the algebras that matches what `Image` supports, along with @:api(doodle.algebra.Text). We then have steps to follow to firstly create a picture and then to use the picture with a concrete backend.

To create a picture:

1. We write a method that has two types parameters, one of which is a type parameter that is a subtype of the algebra we have chosen, and the other is just `F[_]`. See the code below for the example.
2. We declare the result type of the method as `doodle.algebra.Picture[Alg, Unit]`, where `Alg` is the algebra type we declared in the first step.
3. We then write the code for creating the picture as usual in the body of the method, using `Alg` for `Algebra` and `F` for `Drawing`.

Here's an example. The type declaration is complicated but you don't need to understand it. You can just copy and paste, changing `Basic` and `Text` as appropriate for your case.

```scala mdoc:silent
import doodle.language.Basic
import doodle.algebra.{Picture, Text}

def basicWithText[Alg <: Basic & Text]: Picture[Alg, Unit] = {
  val redCircle = circle[Alg](100).strokeColor(Color.red)
  val rad = text[Alg]("Doodle is rad")
  
  rad.on(redCircle)
}
```

To use this method with a concrete backend we call it providing the backend's `Algebra` and `Drawing` types for the `Alg` and `F` type parameters respectively. Note that method has no parameter list so we do not need to provide any parameters.

```scala mdoc:silent
val java2dPicture = basicWithText[Algebra]
```

We can then `draw` the picture as before. In this case we get the output below.

![Doodle is rad, and so is tagless final style](basic-with-text.png)


## Using Raw Algebras

We never need to call methods on algebras directly. Doodle provides the @:api(doodle.algebra.Picture) abstraction and lots of @:api(doodle.syntax.index) to avoid this. However, if some reason we did want to use algebras directly here is how we would do this. Understanding this does help a bit in understanding the utilities that Doodle provides to avoid using algebras directly.

To use these algebras to create a picture you could write a method with a parameter that is the algebras that you need. For example, if we were to write a simple program using `Layout`, `Style`, and `Shape` we might write the following.

```scala mdoc:silent
import doodle.core._
import doodle.algebra._

// Two red circles beside each other
def twoRedCircles[Alg <: Layout & Style & Shape](algebra: Alg): algebra.Drawing[Unit] = {
  val redCircle = algebra.strokeColor(algebra.circle(100), Color.red)
  
  algebra.beside(redCircle, redCircle)
}
  
```

This is not the most convenient way to write code, so Doodle provides several utilities to make it easier.
