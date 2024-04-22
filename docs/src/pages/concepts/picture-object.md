# Picture Object

Each backend provides an object named `Picture` that provides easy access to constructors. A constructor is any method on an algebra methods that produces a `Drawing[A]` but doesn't have any `Drawing[A]` input parameters. For example, all the methods on the @:api(doodle.algebra.Shape) algebra are constructors because they produce a `Drawing[Unit]` but none of them take a `Drawing` as an input.

The `Picture` object allows us to write

```scala
Picture.circle(100)
```

to call the `circle` method on `Shape`.

Be aware that calling `Picture.circle` will create a `Picture`. Calling the `Shape` algebra directly produces a `Drawing`, which is usually not what we want, so Doodle adds this convenience for the API it expects you to use. 
