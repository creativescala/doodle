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
trait BaseConstructor {
  type Algebra <: doodle.algebra.Algebra
  type Drawing[A]

  type Picture[A] = doodle.algebra.Picture[Algebra, A]
}
