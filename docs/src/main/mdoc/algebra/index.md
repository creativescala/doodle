# Algebras

The core of Doodle is built in "tagless final" style. This is a fancy term for saying the functionality is split into a number of different interfaces (which we call "algebras") and different backends will implement some subset of these interfaces. The interfaces, or algebras, live in the @scaladoc[doodle.algebra](doodle.algebra.index) package. This section describes how the algebra and associated machinery works, so you can create images that use backend-specific features or work across backends using features not available in `Image`.

The details of this are fairly involved, but you do not need to understand the details to use the tools that Doodle provides. This section starts with two recipes you can use to create whatever you want without having to understand too much about Doodle internals. If you are interested in Doodle internals the rest of this section explains how they work.


## What Are Algebras?

Each algebra defines an interface for something related to creating pictures. For example, the @scaladoc[Layout](doodle.algebra.Layout) algebra defines some basic methods for positioning pictures while the @scaladoc[Style](doodle.algebra.Style) algebra defines methods for changing the fill and stroke of a picture. All algebras extend the @scaladoc[Algebra](doodle.algebra.Algebra) base trait, which requires an algebra declare some type that indicates the type of value returned by calling methods on the algebra.

Tagless final style allows for a lot of flexibility. Back ends only implement the algebras that they support, which means that different backends can support different features. This allows us to access the full capabilities of each backend (assuming that someone has taken the time to write an algebra for the features you are interested in!)


## Using Backend Specific Features

Use the following recipe to write code using backend specific features (for example, the @scaladoc[Bitmap](doodle.algebra.Bitmap) algebra which is currently only supported by the @scaladoc[Java 2D](doodle.java2d.index) backend).

The first step is to import the backend and syntax extensions, and some Cats implicits we'll need. We'll use `java2d` as our backend.

```scala mdoc:silent
import doodle.java2d._
import doodle.syntax._
import cats.implicits._
```

Now we can write code in a style very similar to using `Image`. There is one important difference: whenever we create an element of a picture that is not composed of other elements (for example, a primitive shape such as `circle`) we must provide two type parameters. Here is an example:

```scala mdoc:silent
val aCircle = circle[Algebra, Drawing](100) // Circle with diameter 100
```

By convention these two type parameters are always called `Algebra` and `Drawing` respectively, so the above code would work with the SVG backend by simply changing the import from `doodle.java2d._` to `doodle.svg._`

Once we have done this we can write code in a straightforward way. For example, to create two red circles beside each other we could write

```scala mdoc:silent
import doodle.core._ // For Color

val redCircle = circle[Algebra, Drawing](100).strokeColor(Color.red)
val twoRedCircles = redCircle.beside(redCircle)
```

To use the `Bitmap` algebra, and assuming the bitmap we refer to exists on disk, we could write

```scala mdoc:silent
val oldGod = read[Algebra, Drawing]("old-god.png")
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

If we want to use some features that are not availble in `Image` and target multiple backends we need to do a little bit more work. First we need to decide what algebras we need. Let's say we decide to use @scaladoc[Basic](doodle.language.Basic), which is a collection of the algebras that matches what `Image` supports, along with @scaladoc[Text](doodle.algebra.Text). We then have steps to follow to firstly create a picture and then to use the picture with a concrete backend.

To create a picture:

1. We write a method that has two types parameters, one of which is a type parameter that is a subtype of the algebra we have chosen, and the other is just `F[_]`. See the code below for the example.
2. We declare the result type of the method as `doodle.algebra.Picture[Alg, F, Unit]`, where `Alg` is the algebra type we declared in the first step.
3. We then write the code for creating the picture as usual in the body of the method, using `Alg` for `Algebra` and `F` for `Drawing`.

Here's an example. The type declaration is complicated but you don't need to understand it. You can just copy and paste, changing `Basic` and `Text` as appropriate for your case.

```scala mdoc:silent
import doodle.language.Basic
import doodle.algebra.{Picture, Text}

def basicWithText[Alg[x[_]] <: Basic[x] with Text[x], F[_]]: Picture[Alg, F, Unit] = {
  val redCircle = circle[Alg, F](100).strokeColor(Color.red)
  val rad = text[Alg, F]("Doodle is rad")
  
  rad.on(redCircle)
}
```

To use this method with a concrete backend we call it providing the backend's `Algebra` and `Drawing` types for the `Alg` and `F` type parameters respectively. Note that method has no parameter list so we do not need to provide any parameters.

```scala mdoc:silent
val java2dPicture = basicWithText[Algebra, Drawing]
```

We can then `draw` the picture as before. In this case we get the output below.

![Doodle is rad, and so is tagless final style](basic-with-text.png)


## Using Raw Algebras

We never need to call methods on algebras directly. Doodle provides the @scaladoc[Picture](doodle.algebra.Picture) abstraction and lots of @scaladoc[syntax](doodle.syntax) to avoid this. However, if some reason we did want to use algebras directly here is how we would do this. Understanding this does help a bit in understanding the utilities that Doodle provides to avoid using algebras directly.

To use these algebras to create a picture you could write a method with a parameter that is the algebras that you need. For example, if we were to write a simple program using `Layout`, `Style`, and `Shape` we might write the following.

```scala mdoc:silent
import doodle.core._
import doodle.algebra._

// Two red circles beside each other
def twoRedCircles[Alg[x[_]] <: Layout[x] with Style[x] with Shape[x], F[_]](algebra: Alg[F]): F[Unit] = {
  val redCircle = algebra.strokeColor(algebra.circle(100), Color.red)
  
  algebra.beside(redCircle, redCircle)
}
  
```

This is not the most convenient way to write code, so Doodle provides several utilities to make it easier.


## Picture and Syntax

Using methods is not very convenient as methods are not values; we cannot pass a method to a method or return a method from a method. Functions are values, but in Scala 2 we cannot have a function parameter that is also an implicit parameter (Scala 3 solves this with [context functions][context-functions]). @scaladoc[Picture](doodle.algebra.Picture) is an abstraction that provides something close to a function with an implicit parameter. It has three type parameters. They are, in order:

- The type `Alg` of the algebras that the picture needs to draw. This is an input to the `Picture` and hence it is contravariant.
- The type of the effect `F` that the picture will create.
- The type `A` of the result the picture will produce when it is drawn. This is usually `Unit`, as we normally draw pictures just for their effect, but it could be something else.

Thus in effect a `Picture` is a function with type `Alg[F] => F[A]`

Doodle provides syntax for all of its algebras that works with `Picture` to avoid us having to create instances of `Picture` by hand. However we do sometimes need to write down the type of a result to avoid confusing the type inference algorithm. The recipes above show when this is needed.


[context-function]: https://dotty.epfl.ch/docs/reference/contextual/context-functions.html
