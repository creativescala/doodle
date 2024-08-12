# Design

The API that Doodle presents to the user should be compositional and easy to reason about. 
There are fundamental ideas in functional programming.
Internally, the implementation may be a stateful program, as the majority of graphics library are implemented in a stateful way.


## Conveniences

We provide convenience operations for common operations. 
For example, we can just `draw` a `Picture` without providing a `Canvas` or `Frame`, or having to deal with `IO`.

Conveniences that run `IO` should be implemented as syntax.
Other conveniences should generally be implemented as methods on algebras,
implemented in terms of more basic methods on the algebra.
(Note that the current system is very mixed on whether this is followed or not.)

The default operations, provided as syntax, generally do not expose `IO`. 
Altneratives that do expose `IO` are suffixed `toIO`.
So, for example, `draw` is the default operation to render a `Picture` and does not expose an `IO`.
The syntax that does is called `drawToIO`.
