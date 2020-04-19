# Picture

@scaladoc[Picture](doodle.algebra.Picture) is an imitation of a function whose input is an implicit parameter (which in Dotty would just be a [context function][context-function]). It has three type parameters. They are, in order:

- The type `Alg` of the algebras that the picture needs to draw. This is an input to the `Picture` and hence it is contravariant.
- The type of the effect `F` that the picture will create.
- The type `A` of the result the picture will produce when it is drawn. This is usually `Unit`, as we normally draw pictures just for their effect, but it could be something else.

Thus in effect a `Picture` is a function with type `Alg[F] => F[A]`.


## Pictures Are Values

Algebras *do not* work directly with `Picture`. Instead they work with the `F[A]` type that is the output of a `Picture`. However, all the @scaladoc[syntax](doodle.syntax.index) that makes the algebras easier to use, and which we have used in our previous examples create and consume `Picture`. The reason for this is that working with raw algebras requires we wrap everything in methods. Methods are not values; we cannot pass a method to a method nor return a method from a method. Functions are values, but in Scala 2 their input parameters cannot also be implicit parameters. `Picture` is like a function with an implicit input parameter. It also provides a bit more structure than using functions directly. When we see a `Picture` we know exactly what we're dealing with.

[context-function]: https://dotty.epfl.ch/docs/reference/contextual/context-functions.html


## Writing and Converting Pictures


## Converting Other Types to a Picture

The @scaladoc[ToPicture](doodle.algebra.ToPicture) algebra provides a single method that converts some type (fixed for each instance of `ToPicture`) to a `F[Unit]`. The @scaladoc[corresponding syntax](doodle.syntax.ToPictureSyntax) wraps the result in a `Picture`, as is standard for syntax, and therefore gives a way to convert the input type to a `Picture`.

The available instances vary depending on the backend. For the Java2D backend, @scaladoc[Base64](doodle.core.Base64) and `BufferedImage` values can be converted to `Picture`. For SVG only `Base64` is currently supported.

Here is quick example of use. First we create a `Base64` value from a `Picture`.

```scala mdoc:silent
import doodle.core._
import doodle.syntax._
import doodle.java2d._
import doodle.effect.Writer._
```
```scala mdoc
// The value we throw away is the result of evaluating the `Picture`, which is `Unit`.
val (_, base64) = circle[Algebra, Drawing](100).base64[Png]()
```

We can convert it right back to a `Picture` using the `toPicture` method.

```scala
val picture = base64.toPicture
```


## Type Class Instances for Picture

There are a few type class instances defined for `Picture`.

`Picture[Alg,F,?]` has a `Monoid` instance if:

- the algebra has `Layout` and `Shape`;
- the effect type `F` has a `Functor`; and
- and the result type has a `Monoid`.
   
In this case the combine is `on`, with identity `empty`.

`Picture[Alg,F,?]` has a `Monad` instance if `F` does.
