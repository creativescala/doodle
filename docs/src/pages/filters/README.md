# Filters

```scala mdoc:invisible
import doodle.core._
import doodle.syntax.all._
```

Filter effects allow you to apply image processing operations like blur, edge detection, and convolutions to Doodle pictures. These effects enhance visual appearance by manipulating how shapes and paths are rendered.

## Overview

Doodle's filter system follows the same architectural principles as other features:

- **Cross-platform**: Works across SVG, Java2D, and Canvas backends.
- **Composable**: Effects can be chained and combined.
- **Type-safe**: Leverages Scala's type system for compile-time correctness.
- **Functional**: Pure, immutable transformations.

## Basic Usage

```scala mdoc:silent
// Create a basic shape
val shape = circle(100).fillColor(Color.red)

// Apply filter effects
val blurred = shape.blur(5.0)
val sharpened = shape.sharpen(2.0)
val edges = shape.detectEdges
val shadowed = shape.dropShadow(10, 10, 5, Color.black.alpha(Normalized(0.5)))

// Chain multiple effects
val complex = shape
  .blur(2.0)
  .sharpen(1.5)
  .dropShadow(8, 8, 4, Color.black.alpha(Normalized(0.3)))
```

## Available Effects

**Basic Filters:**
- `blur(stdDev)` - Gaussian blur with configurable intensity.
- `boxBlur(radius)` - Uniform blur using convolution.
- `sharpen(amount)` - Edge enhancement.
- `detectEdges` - Laplacian edge detection.
- `emboss` - 3D raised surface effect.

**Advanced Effects:**
- `dropShadow(x, y, blur, color)` - Adds depth and dimension.
- `convolve(kernel)` - Custom convolution with user-defined kernels.

## Custom Convolutions

For advanced effects, create custom kernels:

```scala mdoc:silent
import doodle.algebra.Kernel

// Edge enhancement kernel
val enhance = Kernel(3, 3, IArray(
  -1, -1, -1,
  -1,  9, -1,
  -1, -1, -1
))

val enhanced = shape.convolve(enhance)
```

## Backend Support

Filter effects are implemented differently across backends to leverage each platform's strengths:

- **[SVG](svg.md)**: Uses native SVG filter primitives (`<feGaussianBlur>`, `<feConvolveMatrix>`).
- **Java2D**: Leverages `BufferedImage` and `ConvolveOp` for pixel-level operations.
- **Canvas**: Manual pixel manipulation for browser-based rendering.

Each backend provides the same API while optimizing for platform capabilities.

## Performance Considerations
- **Kernel Size**: Larger kernels impact performance significantly.
- **Effect Chains**: Each filter adds processing overhead.

Choose effects and parameters based on your performance requirements.

## Next Steps

1. **Bitmap Refactoring**.
2. **Complete Java2D Implementation**: Full convolution support using `BufferedImage`.
3. **Canvas Backend**: Manual pixel manipulation for ScalaJS.
4. **Color Matrix Filters**: Hue, saturation, brightness adjustments.
5. **Effect Gallery**: Visual examples of all available filters.
