# Blend Modes

The SVG backend supports [CSS-style blend modes][css-blend].
See the [general blend mode documentation](../pictures/blend.md) for a description of the available blend modes and usage examples.


## Implementation Notes

Blend modes are rendered using the [CSS `mix-blend-mode`][mix-blend-mode] property. At the time of writing there are still a few browsers, mainly on mobile devices, that don't support this feature on SVG elements. Check [compatibility] for your use case.


[css-blend]: https://developer.mozilla.org/en-US/docs/Web/CSS/Reference/Values/blend-mode
[mix-blend-mode]: https://developer.mozilla.org/en-US/docs/Web/CSS/Reference/Properties/mix-blend-mode
[compatibility]: https://developer.mozilla.org/en-US/docs/Web/CSS/Reference/Properties/mix-blend-mode#browser_compatibility
