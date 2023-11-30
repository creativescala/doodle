# Developing Doodle

This section contains notes for developers who want to work on Doodle.


## Implementing Syntax

Every algebra should have associated syntax. 
Implementing syntax is a little bit more involved than defining an algebra due to the extra work we do to make type inference work nicely.
The goal is to build up the algebra required by a `Picture` based on the operations used.
For example, notice the inferred type of the `Picture` below.

``` scala mdoc:silent
import doodle.core.*
import doodle.syntax.all.*
```
``` scala mdoc
circle(100).beside(square(20)).strokeColor(Color.red)
```

This type reflects exactly the algebras used in constructing the `Picture`. 
For users working with a single backend this is never an issue as they work from the constructors on the `Picture` object which start with all the algebras supported by the backend.
However for users working across backends this is essential to avoid a lot of type juggling.

The pattern for implementing syntax so that this work is as follows.

1. Start with an `implicit class` extending a `Picture` with a polymorphic `Algebra` type parameter

   ``` scala
   implicit class ExampleOps[Alg <: Algebra, A](picture: Picture[Alg, A]) {
   ```

2. Methods on the `implicit class` return a `Picture` with additional algebras that the method requires

   ``` scala
     def strokeColor(color: Color): Picture[Alg with Style] = ???
   }
   ```

Constructors, like `circle`, don't need to be defined on an implicit class. They don't *need* a polymorphic `Algebra` type parameter but I've implemented them all with one just in case someone wants to fix that parameter to help with type inference. Here is an example:

``` scala
def rectangle[Alg <: Shape](
    width: Double,
    height: Double
): Picture[Alg, Unit] =
  new Picture[Alg, Unit] {
    def apply(implicit algebra: Alg): algebra.Drawing[Unit] =
      algebra.rectangle(width, height)
  }
```

Binary operations, such as `on`, require *two* polymorphic `Algebra` type parameters.
Here's the implementation of `on` showing this (`Alg` and `Alg2`).

``` scala
implicit class LayoutPictureOps[Alg <: Algebra, A](
    picture: Picture[Alg, A]
) {
  def on[Alg2 <: Algebra](
      that: Picture[Alg2, A]
  )(implicit s: Semigroup[A]): Picture[Alg with Alg2 with Layout, A] =
    new Picture[Alg with Alg2 with Layout, A] {
      def apply(implicit
          algebra: Alg with Alg2 with Layout
      ): algebra.Drawing[A] =
        algebra.on(picture(algebra), that(algebra))
    }
}
```
