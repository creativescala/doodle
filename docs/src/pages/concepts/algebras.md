# Algebras

The majority of the functionality in Doodle is provided by algebras. Algebras are essentially interfaces describing pieces of functionality. A very simple example of an algebra is @:api(doodle.algebra.Shape). It provides the functionality to draw basic geometric shapes, like circles and squares.

Different backends offer different combinations of algebras, reflecting the different features they have available.


## Syntax

Doodle provides so-called syntax to make algebras easier to use. It's because of this syntax that you can write, for example, `circle(10).on(circle(20))` instead of much more complicated expression that explicitly involves algebras. All packages that provide syntax have `syntax` in their name, so you can easily tell when you're bringing them into scope.


## Implementation

All algebras extend @:api(doodle.algebra.Algebra). Algebra doesn't define much that is important, but it serves as a marker that a trait is intended to be an algebra in the Doodle sense. All algebras have a type member `F[_]`, which corresponds to the type of value they produce when a concrete algebra is used to render a picture. 

You usually don't deal directly with algebras, instead working with the easier to use @:api(doodle.algebra.Picture) type, which is also described in the next section.
