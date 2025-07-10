# Filter Effects

```scala mdoc:invisible
import doodle.core._
import doodle.syntax.all._
```

Filter effects transform your pictures with image processing operations like blur, edge detection, and shadows. These effects enhance visual appearance by manipulating how shapes are rendered.

## Blur

The `blur` method creates a soft, out-of-focus effect using Gaussian blur:

```scala mdoc:silent
val circleShape = circle(80).fillColor(Color.red)
val blurredCircle = circleShape.blur(5.0)
```

@:doodle("blur-demo", "SvgBlurDemo.draw")

You can control the blur intensity - higher values create stronger blur effects:

@:doodle("blur-intensities", "SvgBlurIntensities.draw")

## Sharpen

The `sharpen` method enhances edges and details:

```scala mdoc:silent
val compositeShape = circle(60).on(square(80))
  .fillColor(Color.purple)
  .strokeColor(Color.black)
  .strokeWidth(3)

val sharpenedShape = compositeShape.sharpen(2.0)
```

@:doodle("sharpen-demo", "SvgSharpenDemo.draw")

The sharpen amount parameter controls the intensity of the effect. Values above 1.0 increase sharpness, while values between 0 and 1 reduce it.

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
val embossShape = regularPolygon(6, 80).on(circle(100))
  .fillGradient(
    Gradient.radial(
      Point(0, 0), Point(0, 0), 50,
      List((Color.lightBlue, 0.0), (Color.darkBlue, 1.0)),
      Gradient.CycleMethod.NoCycle
    )
  )

val embossedShape = embossShape.emboss
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

When combining filters, consider the order of operations, but blur before sharpen creates a different effect than sharpen before blur.

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
