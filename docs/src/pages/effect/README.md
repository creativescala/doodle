# Effects

A picture is a description of what should be drawn. An effect carries out the description by, for example, drawing a picture to the screen or writing it to a file. Effects are implemented as type classes and defined in the @:api(doodle.effect) package. Users will not usually interact directly with effects, but instead work with the syntax defined for them. For example calling `draw` on a `Picture` is syntax that uses the @:api(doodle.effect.Renderer) effect.

The main effects are:

* @:api(doodle.effect.Renderer), which draws a `Picture` to the screen.
* @:api(doodle.effect.Writer), is a marker trait for effects that convert a `Picture` to some other type or write it to a file.
* @:api(doodle.effect.FileWriter), for writing to a `File`.
* @:api(doodle.effect.Base64Writer), for writing to a base 64 encoded value.
* @:api(doodle.effect.BufferedImageWriter), for writing to a `BufferedImage` on the JVM.
* @:api(doodle.effect.DefaultFrame), not strictly an effect but a useful for utility to obtain a default frame instance. This makes other operations more convenient, as the user does not need to specify a frame.
