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

## Emboss

The `emboss` method creates a 3D raised surface effect:

```scala mdoc:silent
val squareShape = square(100).fillColor(Color.blue)
val embossedSquare = squareShape.emboss
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

## Custom Convolutions

For advanced effects, create custom kernels with the `convolve` method:

```scala mdoc:silent
import doodle.algebra.Kernel

// Edge enhancement kernel
val enhance = Kernel(3, 3, IArray(
  -1, -1, -1,
  -1,  9, -1,
  -1, -1, -1
))

val customShape = circle(60).on(square(80))
  .fillColor(Color.purple)
  .strokeColor(Color.black)
  .strokeWidth(3)

val enhancedShape = customShape.convolve(enhance)
```

@:doodle("custom-kernel-demo", "SvgCustomKernelDemo.draw")

## Box Blur

The `boxBlur` method provides an alternative blur implementation:

```scala mdoc:silent
val orangeCircle = circle(80).fillColor(Color.orange)
val boxBlurred = orangeCircle.boxBlur(5)
```

@:doodle("box-blur-comparison", "SvgBoxBlurComparison.draw")

Box blur creates a uniform blur effect, while Gaussian blur produces a smoother, more natural result.
