# Algebras

@@@ index

- [Using Algebras](using.md)
- [Picture](picture.md)

@@@

The core of Doodle is built in "tagless final" style. This is a fancy term for saying the functionality is split into a number of different interfaces (which we call "algebras") and different backends will implement some subset of these interfaces. The interfaces, or algebras, live in the @scaladoc[doodle.algebra](doodle.algebra.index) package. This section describes how the algebra and associated machinery works, so you can create images that use backend-specific features or work across backends using features not available in `Image`.

The details of this are fairly involved, but you do not need to understand the details to use the tools that Doodle provides. This section starts with two recipes you can use to create whatever you want without having to understand too much about Doodle internals. If you are interested in Doodle internals the rest of this section explains how they work.


