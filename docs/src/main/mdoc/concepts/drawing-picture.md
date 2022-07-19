# Drawing and Picture

Each Doodle backend defines, by convention, two types called `Picture` and `Drawing`. These are the main types you'll interact with.

A `Picture` is a first class value representing the description of the picture we want to draw. We usually work with a type like `Picture[Unit]`, which is a description of a picture that, when drawn, will produce a value of type `Unit`.

A `Picture` is conceptually a function from an `Algebra` to an effect that will actually draw the picture. It's not actually a function because the input parameter---the algebra---is an implicit parameter. In Scala 3 this is a [context function][context-function]. As Doodle supports both Scala 2 and Scala 3 we can't use this language feature and have a custom type instead.

Expanding a little bit further, each backend's `Picture` type is a specialization of the generic @scaladoc[Picture](doodle.algebra.Picture).

The generic `Picture` has type signature 

```scala
trait Picture[-Alg[x[_]] <: Algebra[x], F[_], A]
```

where 

- `Alg` is the type of the algebras the `Picture` requires;
-`F` is the effect type that actually draws the `Picture`, which we call `Drawing` and explain below; and
- `A` is the type of the result that produced when the `Drawing` is drawn, which is usually `Unit`.

Each backend specializes the full `Picture` type to fix `Alg` to the algebras the backend supports, and `F` to the backend's drawing type. This means that users, so long as they only target a single backend, can use a simpler `Picture[A]` type.

The `Drawing` type is the effect type that the backend transforms a `Picture` into. A `Drawing[A]` will draw a picture and produce a value of type `A` when it is run. It is needed much less often than `Picture`. Whenever you see the `Drawing` type (`F` in an `Algebra`)


[context-function]: https://docs.scala-lang.org/scala3/reference/contextual/context-functions.html
