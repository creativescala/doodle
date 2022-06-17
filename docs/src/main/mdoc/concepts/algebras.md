# Algebras

Algebras are essentially interfaces that describe functionality that Doodle offers. For example, a very simple algebra is @scaladoc[Shape](doodle.algebra.Shape). Shape provides the functionality to draw simple geometric shapes, like circles and squares.

Different backends offer different combinations of algebras, reflecting the different features they have available.


## Implementation

All algebras extend @scaladoc[Algebra](doodle.algebra.Algebra). Algebra doesn't define anything important, but it serves as a marker that a trait is intended to be an algebra in the Doodle sense.

All algebras are parameterized by an effect type `F[_]`, which is the type that
