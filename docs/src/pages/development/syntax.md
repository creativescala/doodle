# Implementing Syntax

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

There are different patterns for syntax for constructors and combinators. Syntax should always be defined inside a `trait` that is mixed into the relevant `all` object.


## Constructor Syntax

Constructor syntax is simply a method that produces a `Picture` with the relevant type.
For example, if we have a constructor called `square` which relates to an algebra called `Shape`, we can implement syntax as

```scala 
def square(width: Double): Picture[Shape, Unit] =
  new Picture[Shape, Unit] {
    def apply(implicit algebra: Shape): algebra.Drawing[Unit] =
      algebra.square(width, height)
  }
```


## Combinator Syntax

The pattern for implementing combinator syntax is more involved, as we must worry about type inference. Here's the pattern:

1. Start with an `extension` extending a `Picture` with a polymorphic `Algebra` type parameter

   ``` scala
   extension ExampleOps[Alg <: Algebra, A](picture: Picture[Alg, A]) {
   ```

2. Methods on the `extension` return a `Picture` with additional algebras that the method requires

   ``` scala
     def strokeColor(color: Color): Picture[Alg with Style] = ???
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
