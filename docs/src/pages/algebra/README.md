# Algebras

The core of Doodle is built in "tagless final" style. This is a fancy term for saying the functionality is split into a number of different interfaces (the [algebras](../concepts/algebras.md)) and different backends will implement some subset of these interfaces. The interfaces, or algebras, live in the @:api(doodle.algebra) package. This section describes how the algebra and associated machinery works, so you can create images that use backend-specific features or work across backends using features not available in `Image`.

The details of this are fairly involved, but you do not need to understand the details to use the tools that Doodle provides. This section starts with two recipes you can use to create pictures without having to understand too much about Doodle internals. It then describes in more detail the other components of the algebra machinery.


