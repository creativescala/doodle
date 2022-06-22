# Picture Object

Each backend provides an object named `Picture` that contains constructors. Constructors are methods on algebras that produce a `Drawing` but don't have a `Drawing` parameter. For example, all the methods on the @scaladoc[Shape](doodle.algebra.Shape) algebra are constructors. So to call the `circle` method on `Shape` you'll usually just write

```scala
Picture.circle(100)
```

There is a subtle difference
