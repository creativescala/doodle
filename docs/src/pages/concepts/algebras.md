# Algebras

The majority of the functionality in Doodle is provided by algebras. Algebras are essentially interfaces describing pieces of functionality. A very simple example of an algebra is @:api(doodle.algebra.Shape). It provides the functionality to create basic geometric shapes, like circles and squares.

Different backends offer different combinations of algebras, reflecting the different features they have available.


## Implementation

All algebras extend @:api(doodle.algebra.Algebra). One role of `Algebra` is to serve as a marker that a trait is intended to be an algebra in the Doodle sense. `Algebra` also defines a type member `Drawing[_]`, which corresponds to the type of value produced when a concrete algebra is used to create a picture. This is described in more detail in the next section.

You usually don't deal directly with algebras, instead working with the easier to use @:api(doodle.algebra.Picture) type, which is described in the next section.
