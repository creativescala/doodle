# Convolution Filters

```scala mdoc:invisible
import cats.syntax.all.*
import doodle.core.*
import doodle.random.{*, given}
import doodle.svg.*
import doodle.syntax.all.*
```


## Concept

Convolution filters, or filters for short, transform pictures at the level of individual pixels. They work by replacing each pixel with a weighted sum of its neighboring pixels. The  weights are determined by two-dimensional matrix known as a kernel. Filters can be used for a variety of image processing operations and creative effects such as blurring, edge detection, and drop shadows. 

The filter functionality is provided by the @:api(doodle.algebra.Filter) algebra. It provides a method to apply a user-defined @:api(doodle.algebra.Kernel) to a `Picture`, but also conveniences to apply common operations, like a Gaussian blur, without having to construct a @:api(doodle.algebra.Kernel). These conveniences may also have more efficient implementations, depending upon the backend.


## Blur

The `blur` method creates a soft, out-of-focus effect using a Gaussian blur:

```scala mdoc:silent
val circleShape = circle(80).fillColor(Color.red)
val blurredCircle = circleShape.blur(5.0)
```

@:doodle("blur-demo", "SvgBlurDemo.draw")

The argument to `blur` controls the intensity of the effect, as shown below. 

@:doodle("blur-intensities", "SvgBlurIntensities.draw")


## Sharpen

The `sharpen` method enhances edges and details:

```scala mdoc:silent
val randomCircle: Random[Picture[Unit]] =
  for {
    x <- Random.int(-100, 100)
    y <- Random.int(-100, 100)
    r <- Random.int(15, 45)
    l <- Random.double(0.2, 0.8)
    c <- Random.double(0.1, 0.4)
    h <- Random.double(0.0, 0.3).map(_.turns)
  } yield Picture
    .circle(r)
    .at(x, y)
    .noStroke
    .fillColor(Color.oklch(l, c, h, 0.5))

val randomCircles = randomCircle.replicateA(200).map(_.allOn.margin(20)).run()

val sharpenedShape = randomCircles.sharpen(4.0)
```

@:doodle("sharpen-demo", "SvgSharpenDemo.draw")

The method's parameter controls the intensity of the effect. Values above 1.0 increase sharpness, while values between 0 and 1 reduce it.


## Edge Detection

The `detectEdges` method highlights boundaries and contours:

```scala mdoc:silent
val layeredShape = circle(60).on(square(100))
  .fillColor(Color.lightBlue)
  .strokeColor(Color.darkBlue)
  .strokeWidth(4)

val edgeDetected = layeredShape.detectEdges
```

@:doodle("edge-detection-demo", "SvgEdgeDetectionDemo.draw")

Edge detection is particularly effective on shapes with color gradients or multiple overlapping elements.


## Emboss

The `emboss` method creates a 3D raised surface effect:

```scala mdoc:silent
val concentricCircles = {
  def loop(count: Int): Picture[Unit] =
    count match {
      case 0 => Picture.empty
      case n =>
        Picture
          .circle(n * 15)
          .fillColor(Color.crimson.spin(10.degrees * n).alpha(0.7.normalized))
          .strokeColor(
            Color.red.spin(15.degrees * n).alpha(0.7.normalized)
          )
          .strokeWidth(4.0)
          .under(loop(n - 1))
    }

  loop(7)
}
val embossedShape = concentricCircles.emboss
```

@:doodle("emboss-demo", "SvgEmbossDemo.draw")


## Drop Shadow

The `dropShadow` method adds depth and dimension:

```scala mdoc:silent
val starShape = star(5, 50, 25).fillColor(Color.gold)
val shadowedStar = starShape.dropShadow(
  offsetX = 8,
  offsetY = 8,
  blur = 4,
  color = Color.black.alpha(Normalized(0.5))
)
```

@:doodle("drop-shadow-demo", "SvgDropShadowDemo.draw")

You can control the shadow's position (`offsetX`, `offsetY`), softness (`blur`), and appearance (`color` with alpha transparency).


## Combining Effects

Filter effects can be chained to create complex transformations:

```scala mdoc:silent
val hexagon = regularPolygon(6, 60)
  .fillColor(Color.crimson)
  .strokeColor(Color.white)
  .strokeWidth(3)

val multiFiltered = hexagon
  .blur(2.0)
  .sharpen(1.5)
  .dropShadow(10, 10, 3, Color.black.alpha(Normalized(0.4)))
```

@:doodle("chained-filters", "SvgChainedFilters.draw")

The order of operations is important when combining filters. For example, blur before sharpen creates a different effect to sharpen before blur.


## Custom Convolutions

For advanced effects, create custom kernels with the `convolve` method. A kernel is a matrix of values that determines how each pixel is combined with its neighbors:

```scala mdoc:silent
import doodle.algebra.Kernel

// Custom sharpening kernel
val customSharpen = Kernel(3, 3, IArray(
   0, -2,  0,
  -2,  9, -2,
   0, -2,  0
))

val customStarShape = star(6, 60, 30).on(circle(80))
  .fillGradient(
    Gradient.linear(
      Point(-40, -40), Point(40, 40),
      List((Color.purple, 0.0), (Color.hotPink, 0.5), (Color.orange, 1.0)),
      Gradient.CycleMethod.NoCycle
    )
  )

val enhancedShape = customStarShape.convolve(customSharpen)
```

@:doodle("custom-kernel-demo", "SvgCustomKernelDemo.draw")

Convolution kernels work by multiplying each pixel and its neighbors by the corresponding kernel values, then summing the results. Common kernel patterns include:
- **Edge detection**: negative values around a positive center.
- **Blur**: all positive values that sum to 1.
- **Sharpen**: negative values around a center value greater than.


## Box Blur

The `boxBlur` method provides an alternative blur implementation. Unlike Gaussian blur which creates a smooth falloff, box blur averages pixels uniformly within a square area:

```scala mdoc:silent
val orangeCircle = circle(80).fillColor(Color.orange)
val boxBlurred = orangeCircle.boxBlur(5)
```

@:doodle("box-blur-comparison", "SvgBoxBlurComparison.draw")

Box blur creates a uniform blur effect, while Gaussian blur produces a smoother, more natural result.
