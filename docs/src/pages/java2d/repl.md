# Working at the REPL

The Java2D backend provides a utility for working at the REPL (also known as the
console). Using the following imports

```scala
import cats.effect.unsafe.implicits.global
import doodle.core.*
import doodle.java2d.repl.{*, given}
import doodle.syntax.all.*
```

(notice that `doodle.java2d.{*, given}` has changed to `doodle.java2d.repl.{*,
given}`) will change the behaviour of the default `Frame`. Instead of waiting
until the window is closed before returning control after drawing a `Picture`,
control will return immediately. This is useful when working in the REPL and you
want to quickly view pictures as you make changes.
