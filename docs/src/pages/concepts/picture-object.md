# Picture Object

Each backend provides an object named `Picture` that provides easy access to constructors. Constructors are algebra methods that produce a `Picture` but don't have any `Drawing[A]` parameter. For example, all the methods on the @:api(doodle.algebra.Shape) algebra are constructors. 

The `Picture` object allows us to write

```scala
Picture.circle(100)
```

to call the `circle` method on `Shape` instead of the more verbose

```scala
circle[Algebra, Drawing](100)
```

Both of these methods create a `Picture`. If we were to call the `Shape` algebra directly we'd end up with a `Drawing`, which is usually not what we want, so Doodle adds this convenience for the API it expects you to use. 
