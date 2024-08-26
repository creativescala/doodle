# Design

The API that Doodle presents to the user should be compositional and easy to reason about. 
There are fundamental ideas in functional programming.
Internally, the implementation may be a stateful program, as the majority of graphics library are implemented in a stateful way.


## Conveniences

We provide convenience operations for common operations. 
For example, we can just `draw` a `Picture` without providing a `Canvas` or `Frame`, or having to deal with `IO`.
The most convenient operation is usually presented as the default. 
So we expect people will want to use `draw` more than `drawWithFrame` or `drawToIO`, and hence it has the shortest name and is introduced first in documentation.

Conveniences should be implemented as syntax.
Try to keep algebras to the minimal set of most basic operations.
Note that the current system doesn't follow this advice very closely.
Until we figured how to make the current architecture it was expected that people would frequently interact directly with algebras,
so they tend to contain more methods than is strictly necessary.

The default operations, provided as syntax, generally do not expose `IO`. 
Altneratives that do expose `IO` are suffixed `toIO`.
So, for example, `draw` is the default operation to render a `Picture` and does not expose an `IO`.
The syntax that does is called `drawToIO`.

There are a couple of naming conventions in use:

* operations that require more parameters than the default are suffixed with `withA`, where `A` is the type. E.g. `drawWithFrame`.
* operations that produces a different output type are suffixed with `toA`, where `A` is the type. E.g. `drawToIO`.
* the above can be combined, as in `drawWithFrameToIO`.
