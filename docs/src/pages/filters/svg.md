# SVG Filters

```scala mdoc:invisible
import doodle.core._
import doodle.syntax.all._
import doodle.algebra.{Filter, Kernel}
```

The SVG backend implements filter effects using native SVG filter primitives, providing hardware-accelerated performance and seamless integration with web browsers.

## SVG Filter Architecture

Each filter effect generates:

1. A unique filter definition in `<defs>`.
2. A `<g>` group wrapping the element with `filter="url(#filterId)"`.

### Generated SVG Structure

```xml
<defs>
  <filter id="blur-a1b2c3">
    <feGaussianBlur in="SourceGraphic" stdDeviation="5"/>
  </filter>
</defs>
<g filter="url(#blur-a1b2c3)">
  <!-- Original shape here -->
</g>
```

## Available Filters

### Gaussian Blur

Uses SVG's native `<feGaussianBlur>` for optimal performance:

```scala mdoc:silent
val shape = circle(100).fillColor(Color.red).beside(
  square(100).fillColor(Color.blue)
)

// Different blur intensities
val lightBlur = shape.blur(2.0)
val mediumBlur = shape.blur(5.0)
val heavyBlur = shape.blur(10.0)
```

The parameter is the standard deviation of the gaussian distribution.

### Box Blur

Uniform blur implemented via `<feConvolveMatrix>`:

```scala mdoc:silent
val boxBlur1 = shape.boxBlur(1)  // 3×3 kernel
val boxBlur2 = shape.boxBlur(2)  // 5×5 kernel
val boxBlur3 = shape.boxBlur(3)  // 7×7 kernel
```

Box blur creates a uniform kernel where all values are `1 / (width × height)`.

### Edge Detection

Highlights edges using a Laplacian kernel:

```scala mdoc:silent
val edges = shape.detectEdges
```

Uses this kernel internally:
```
 0  -1   0
-1   4  -1
 0  -1   0
```

### Sharpen

Enhances edge contrast:

```scala mdoc:silent
val sharpened = shape.sharpen(1.0)      // subtle
val moreSharpened = shape.sharpen(2.0)  // stronger
```

The amount parameter scales the sharpening kernel.

### Emboss

Creates a 3D raised effect:

```scala mdoc:silent
val embossed = shape.emboss
```

Uses this kernel:
```
-2  -1   0
-1   1   1
 0   1   2
```

### Drop Shadow

Combines multiple SVG filter primitives for depth:

```scala mdoc:silent
val shadow = shape.dropShadow(
  offsetX = 10.0,
  offsetY = 10.0,
  blur = 5.0,
  color = Color.black.alpha(Normalized(0.5))
)

// Colored shadow
val coloredShadow = shape.dropShadow(
  offsetX = 5.0,
  offsetY = 5.0,
  blur = 3.0,
  color = Color.red.alpha(Normalized(0.3))
)
```

## Custom Convolution Kernels

### Kernel Structure

The `Kernel` type enforces proper convolution matrix structure:

```scala mdoc
// Valid 3×3 kernel
val identity = Kernel(3, 3, IArray(
  0, 0, 0,
  0, 1, 0,
  0, 0, 0
))

// Non-square kernels are supported
val horizontalBlur = Kernel(5, 1, IArray(0.2, 0.2, 0.2, 0.2, 0.2))
val verticalBlur = Kernel(1, 5, IArray(0.2, 0.2, 0.2, 0.2, 0.2))
```

### Kernel Requirements

```scala mdoc:silent
// These patterns are invalid and would fail at runtime:
// Kernel(2, 2, IArray(1, 2, 3, 4))        // Even dimensions - no center pixel!
// Kernel(3, 3, IArray(1, 2, 3, 4))        // Wrong element count (needs 9)

// Valid kernels must have odd dimensions and correct element count:
val validKernel = Kernel(3, 3, IArray.fill(9)(1.0/9))
```

### Custom Effects

```scala mdoc:silent
// Strong edge enhancement
val edgeEnhance = Kernel(3, 3, IArray(
  -1, -1, -1,
  -1,  9, -1,
  -1, -1, -1
))

// Directional blur
val diagonalBlur = Kernel(3, 3, IArray(
  0.5, 0.0, 0.0,
  0.0, 0.0, 0.0,
  0.0, 0.0, 0.5
))

// Apply custom kernels
val enhanced = shape.convolve(edgeEnhance)
val diagonal = shape.convolve(diagonalBlur)
```

## Predefined Kernels

```scala mdoc
import doodle.algebra.Filter

// Standard kernels available
val gaussian = Filter.gaussianKernel3x3
println(s"Gaussian sum: ${gaussian.sum}")

val sharpen = Filter.sharpenKernel
val edgeDetect = Filter.edgeDetectionKernel
val emboss = Filter.embossKernel
val boxBlur = Filter.boxBlurKernel3x3
```

## Combining Filters

Filters can be chained for complex effects:

```scala mdoc:silent
// Blur then add shadow
val blurredWithShadow = shape
  .blur(2.0)
  .dropShadow(8.0, 8.0, 4.0, Color.black.alpha(Normalized(0.4)))

// Multiple effects
val artistic = shape
  .blur(1.0)
  .sharpen(1.5)
  .dropShadow(5.0, 5.0, 2.0, Color.black.alpha(Normalized(0.3)))
```

## SVG Implementation Details

### Order Attribute

The `feConvolveMatrix` order attribute follows SVG specification:
- **Square kernels**: Single value, e.g., `order="3"`.
- **Rectangular kernels**: Two values, e.g., `order="5 3"` (width & height).

```scala mdoc:silent
val square3x3 = Kernel(3, 3, IArray.fill(9)(1.0/9))
// Generates: order="3"

val rect5x3 = Kernel(5, 3, IArray.fill(15)(1.0/15))
// Generates: order="5 3"
```

### Edge Mode

All convolutions use `edgeMode="duplicate"` to handle image boundaries by duplicating edge pixels.

### Number Formatting

The implementation formats numbers consistently across JVM and JS:
- Integers: `"3"` not `"3.0"`.
- Decimals: `"3.14"` as needed.

### Unique ID Generation

Each filter gets a unique ID using:
```scala
val filterId = s"blur-${Random.nextLong().toHexString}"
```

This prevents conflicts when multiple filters are applied.

## Platform-Specific Usage

### Scala.js (Browser)

For browser rendering with Scala.js:

```scala
// Import SVG support
import doodle.svg._

// Render to an existing canvas element
picture.drawWithCanvas("my-canvas-id")

// Or create an SVG frame
val frame = Frame("my-svg-id").size(400, 400)
picture.drawWithFrame(frame)

// The result appears directly in the browser
```

### JVM (File Output)

For file output on the JVM:

```scala
// Additional imports needed
import doodle.svg._
import doodle.java2d._
import java.io.File

// Write to SVG file
picture.write[Svg]("output.svg")
```

## Performance Characteristics

### Optimization Strategies

1. **Kernel Size Impact**:
   - 3×3: Fast, real-time performance.
   - 5×5: Moderate performance.
   - 7×7+: Slower, consider performance implications.

2. **Filter Chains**: Each filter adds a `<g>` group and filter definition.
   - Browsers optimize well, but many filters can impact rendering.
   - Consider combining effects where possible.

3. **Browser Performance**: Generally fastest SVG filter performance.

### Memory Usage

SVG filters are memory-efficient because:
- Filters are vector-based, not raster.
- No pixel buffers created unless rendering to bitmap.
- GPU acceleration available in modern browsers.

## Example Usage

```scala mdoc:silent
// Create a complex filtered composition
def filterShowcase = {
  val base = circle(50).fillColor(Color.red)

  val demos = List(
    ("Original", base),
    ("Blur(3)", base.blur(3.0)),
    ("Sharpen(2)", base.sharpen(2.0)),
    ("Edges", base.detectEdges),
    ("Emboss", base.emboss),
    ("Shadow", base.dropShadow(5, 5, 3)),
    ("Box Blur", base.boxBlur(2)),
    ("Combined", base.blur(1.0).dropShadow(3, 3, 2))
  )

  // Arrange in a grid for comparison
  // When rendered, this shows all effects side by side
}
```
