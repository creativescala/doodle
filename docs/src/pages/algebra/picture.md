# Picture

@:api(doodle.algebra.Picture) is an imitation of a function whose input is an implicit parameter (which in Scala 3 is a [context function][context-function]), and whose output type depends on the input type (which in Scala 3 is a [dependent function type]). It has two type parameters. They are, in order:

- The type `Alg` of the algebras that the picture needs to draw. This is an input to the `Picture` and hence it is contravariant. This type has a type member `Drawing[_]` that is the type of effect the `Picture` produces when it is applied to a concrete algebra.
- The type `A` of the result the picture will produce when it is drawn. This is usually `Unit`, as we normally draw pictures just for their effect, but it could be something else.

In Scala 3 notation, a `Picture` is conceptually equivalent to a function with type `[Alg <: Algebra, A] => (a: Alg) ?=> a.Drawing[A]`.


## Pictures Are Values

Algebras *do not* work directly with `Picture`. Instead they work with the `Drawing[A]` type that is the output of a `Picture`. However, all the @:api(doodle.syntax) that makes the algebras easier to use, and which we have used in our previous examples, create and consume `Picture`. The reason for this is that working with raw algebras requires we wrap everything in methods. Methods are not values; we cannot pass a method to a method nor return a method from a method. Functions are values, but in Scala 2 their input parameters cannot also be implicit parameters. `Picture` is like a function with an implicit input parameter. It also provides a bit more structure than using functions directly. When we see a `Picture` we know exactly what we're dealing with.

[context-function]: https://docs.scala-lang.org/scala3/reference/contextual/context-functions.html
[dependent function type]: https://docs.scala-lang.org/scala3/reference/new-types/dependent-function-types.html

## Drawing Pictures

We can draw a picture to the screen using the `draw` method. This is @:api(doodle.syntax.RendererSyntax) that depends on a having a @:api(doodle.effect.Renderer) in scope. There are other methods provided by the `Renderer` effect, as explained in its documentation.


## Writing and Converting Pictures

In addition to drawing a picture to the screen we can write it to a file or convert it to some other type. The `write` method saves a picture to a file. When we call write we must pass two parameters: a normal parameter that is the file name to use and a type parameter that gives the format of the file. In the example below we save a file as a [PNG][png].

```scala mdoc:compile-only
import doodle.core.*
import doodle.syntax.all.*
import doodle.java2d.*
import doodle.core.format.*
import cats.effect.unsafe.implicits.global

val picture = circle(100)

picture.write[Png]("circle.png")
```

The `write` method is @:api(doodle.syntax.WriterSyntax) that comes from the @:api(doodle.effect.Writer). There are other methods that allow, for example, specifying a `Frame` to use when saving the file.

We can convert a `Picture` to a [Base64][base64] value using the `base64` method. As with `write`, this method is @:api(doodle.syntax.Base64Syntax) for the @:api(doodle.effect.Base64). The parameters are similar to `write`: we must specify a format to encode the picture in but we don't specify a filename. Instead we get back the result of evaluating the `Picture` (the `A` in `F[A]` which is usually `()`) and a @:api(doodle.core.Base64) value.

```scala mdoc:silent:reset
import doodle.core.*
import doodle.syntax.all.*
import doodle.java2d.*
import doodle.core.format.*
import cats.effect.unsafe.implicits.global

val picture = circle(100)

val (result, b64) = picture.base64[Png]()
```


## Converting Other Types to a Picture

The @:api(doodle.algebra.ToPicture) algebra provides a single method that converts some type (fixed for each instance of `ToPicture`) to a `F[Unit]`. The @:api(doodle.syntax.ToPictureSyntax) wraps the result in a `Picture`, as is standard for syntax, and therefore gives a way to convert the input type to a `Picture`.

The available instances vary depending on the backend. For the Java2D backend, @:api(doodle.core.Base64) and `BufferedImage` values can be converted to `Picture`. For SVG only `Base64` is currently supported.

Here is quick example of use. First we create a `Base64` value from a `Picture`.

```scala mdoc:silent:reset
import doodle.core.*
import doodle.syntax.all.*
import doodle.java2d.*
import doodle.core.format.*
import cats.effect.unsafe.implicits.global
```
```scala mdoc:silent
// The value we throw away is the result of evaluating the
// `Picture`, which is `Unit`.
val (_, base64) = circle(100).base64[Png]()
```

We can convert it right back to a `Picture` using the `toPicture` method.

```scala mdoc:silent
val picture = base64.toPicture
```


## Type Class Instances for Picture

There some type class instances defined for `Picture`.

`Picture[Alg,*]` always has an `Monad` instance.

`Picture[Alg,*]` has a `Monoid` instance if:

- the algebra has `Layout` and `Shape`; and
- and the result type has a `Monoid`.
   
In this case the combine is `on`, with identity `empty`.


[png]: https://en.wikipedia.org/wiki/Portable_Network_Graphics
[base64]: https://en.wikipedia.org/wiki/Base64
