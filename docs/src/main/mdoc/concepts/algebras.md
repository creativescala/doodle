# Algebras

The majority of the functionality in Doodle is provided by algbras. Algebras are essentially interfaces describing pieces of functionality that Doodle provides. A very simple example of an algebra is @scaladoc[Shape](doodle.algebra.Shape). `Shape` provides the functionality to draw basic geometric shapes, like circles and squares.

Different backends offer different combinations of algebras, reflecting the different features they have available.


## Syntax

Doodle provides so-called syntax


## Implementation

All algebras extend @scaladoc[Algebra](doodle.algebra.Algebra). Algebra doesn't define anything important, but it serves as a marker that a trait is intended to be an algebra in the Doodle sense.

All algebras are parameterized by an effect type `F[_]`, which corresponds to the `Drawing` type described next. You usually don't deal directly with `Drawing`, instead working with the easier to use `Picture` type, which is also described in the next section.
