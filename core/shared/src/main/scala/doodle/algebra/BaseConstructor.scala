package doodle.algebra

/** Base module for constructors
  *
  * The intention is to assist with type inference for constructors by defining
  * constructors that are parameterized by the Algebra and Drawing types, and
  * instantiating those types within each backend.
  *
  * Algebras that define constructors should also define a constructor mixin.
  * See e.g. Shape for an example.
  */
trait BaseConstructor[A[x[_]] <: doodle.algebra.Algebra[x]] {
  type Algebra[x[_]] = A[x]
  type Drawing[A]

  type Picture[A] = doodle.algebra.Picture[Algebra, Drawing, A]
  object Picture {
    def apply(f: Algebra[Drawing] => Drawing[Unit]): Picture[Unit] = {
      new Picture[Unit] {
        def apply(implicit algebra: Algebra[Drawing]): Drawing[Unit] =
          f(algebra)
      }
    }
  }
}
