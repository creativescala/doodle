# Effects

In Doodle, an effect such as @:api(doodle.effect.Renderer) carries out the instructions in a `Picture`. In the case of `Renderer`, this is drawing the picture on the screen. Converting a `Picture` to another type or saving it to a file are another very common effects managed by `Writers`, for example, a `Base64Writer` or a `FileWriter`.

You will mostly interact with effects by convenient syntax. For example, when you call `draw` on a `Picture` you are using an effect via such a convenience.

