# Picture Object

Each backend provides an object named `Picture` that provides easy access to constructors. Constructors are algebra methods that produce a `Picture` but don't have any `` parameter. For example, all the methods on the @:api(doodle.algebra.Shape) algebra are constructors. 

The `Picture` object allows us to write

```scala
Picture.circle(100)
```

to call the `circle` method on `Shape` instead of the more verbose

```scala
circle[Algebra, Drawing](100)
```

So to call the `circle` method on `Shape` you'll usually just write

```scala
Picture.circle(100)
```

There is a subtle difference between the result of calling `circle` on the `Picture` object and same method on the `Shape` algebra. The former returns a `Picture` while the later returns a `Drawing`.
