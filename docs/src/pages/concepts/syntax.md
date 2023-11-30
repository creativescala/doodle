# Syntax

To use methods on algebras that are not constructors (constructors are explained in [Picture Object](picture-object.md)) you would usually use syntax.

The standard import for syntax is

```scala mdoc:invisible
import doodle.java2d.*
```
```scala mdoc:silent
import doodle.syntax.all.*
```

Say we want to use the `beside` method on @:api(doodle.algebra.Layout). First we need some pictures to layout.

```scala mdoc:silent
val left = Picture.circle(10)
val right = Picture.circle(20)
```

Then, with the syntax imported, we can just call the `beside` method as if it's a method on `Picture`.

```scala mdoc:silent
left.beside(right)
```

As with constructors, syntax methods will return a `Picture` not a `Drawing`.
