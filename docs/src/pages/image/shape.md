# Shapes

## Basic Shapes

The basic shapes are on the `Image` companion object. This includes:

* `Image.empty` creates the empty Image that takes up no space and renders nothing. Useful for that sweet monoid identity and for the base case in recursions.
* `Image.square(sideLength)` creates a square with the given side length.
* `Image.rectangle(width, height)` creates a rectangle with the given width and height.
* `Image.circle(diameter)` creates a circle with the given diameter. Specified in terms of diameter rather than radius so that `Image.square(100)` and `Image.circle(100)` take up the same space.
* `Image.triangle(width, height)` creates an isoceles triangle with the given width and height.


## Complex Shapes

Some more complex shapes are also available on the `Image` companion object:

* `Image.equilateralTriangle(width)` creates an equilateral triangle with the given side length.
* `Image.regularPolygon(sides, radius)` creates a regular polygon with the given number of sides and radius. 
* `Image.star(points, outerRadius, innerRadius)` creates a star with the given number of points. The points extend as far as `outerRadius` and go in to `innerRadius`. 
* `Image.rightArrow(width, height)` creates an arrow points to the right with the given width and height.
* `Image.roundedRectangle(width, height, radius)` creates a rectangle of the given width and height, with rounded corners with size given by `radius`.


## Paths

Paths allow construction of custom shapes. They are described in [their own section](path.md). There are three methods on the `Image` companion object that deal with paths:

* `Image.openPath(pathElements)` converts a `Seq[PathElement]` into an `Image` representing an open path (a path that does not end where it begins).
* `Image.closedPath` converts a `Seq[PathElement]` to an `Image` representing a closed path (a path that ends where it begins; a straight line will be inserted if to make this the case if needed.)
* `Image.interpolatingSpline` converts a `Seq[Point]` to an `Image` by interpolating a smooth curve between the points using the [Catmull-Rom][catmull-rom] algorithm.

[catmull-rom]: https://en.wikipedia.org/wiki/Centripetal_Catmull%E2%80%93Rom_spline
