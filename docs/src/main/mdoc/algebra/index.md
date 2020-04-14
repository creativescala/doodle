# Using Tagless Final Algebras in Doodle

The core of Doodle is built in "tagless final" style. This is a fancy term for saying the functionality is split into a number of different interfaces (which we call "algebras") and different backends will implement some subset of these interfaces. The interfaces, or algebras, live in the @scaladoc[doodle.algebra](doodle.algebra) package.

Each algebra defines an interface for something related to creating pictures. For example, the @scaladoc[Layout](doodle.algebra.Layout) algebra defines some basic methods for positioning pictures while the @scaladoc[Style](doodle.algebra.Style) algebra defines methods for changing the fill and stroke of a picture.
