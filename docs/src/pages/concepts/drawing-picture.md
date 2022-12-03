# Picture and Drawing

Each Doodle backend defines, by convention, two types called @:api(doodle.algebra.Picture) and `Drawing`. These are the main types you'll interact with.

The quick overview is:

- `Picture` is a backend independent description of a picture, that accepts backend specific algebra implementations to produce a backend specific `Drawing`
- `Drawing` is an effect that, when run, draws a picture on a specific backend.

The generic `Picture` has type signature 

```scala
trait Picture[-Alg <: Algebra, A]
```

where 

- `Alg` is the type of the algebras the `Picture` requires;
- `A` is the type of the result produced when the `Picture` is transformed into a `Drawing`, and that `Drawing` is run.

Each backend specializes the full `Picture` type to fix `Alg` to the algebras the backend supports. Most of the time you'll only target a particular backend. In this case you can drop the `Alg` parameter and use a simpler `Picture[A]` type.

A `Picture` is a first class value (a value that can be passed to a method or returned from a method). A `Picture` is conceptually a function from an `Algebra` to a `Drawing[A]`. `Picture` is not implemented as a function, in the Scala sense, because the input parameter (the algebra) is an implicit parameter. In Scala 3 this is a [context function][context-function]. As Doodle supports both Scala 2 and Scala 3 we can't use this language feature and have a custom type instead.

The `Drawing` type is the effect type that the backend transforms a `Picture` into. When run, a `Drawing[A]` will draw a picture and produce a value of type `A`. It is needed much less often than `Picture`. If you see the `Drawing` type you're probably either implementing your own backend or working directly with algebras instead of using the conveniences that Doodle provides.

[context-function]: https://docs.scala-lang.org/scala3/reference/contextual/context-functions.html
