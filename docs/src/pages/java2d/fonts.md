# Fonts and Text

If you want to distribution an application using the Java2D backend, you might wonder how to ensure the fonts you want are available on the user's machine. You can distribute fonts with your application, and make them available with the following steps:

1. Ensure the fonts are packages with your application as resources. This means putting them under `src/main/resources` in a standard sbt build, or for the Scala CLI using a directive like `//> using resourceDir ./resources` and placing the fonts under the `resources` directory.

2. At application startup, load the font from resources. Let's say you're distributing [DejaVu Sans][dejavu-sans] with your application, and you have stored the font file in your resources as `fonts/DejaVu-Sans.ttf`. The following code will load it and create a Java2D font from it.

  ```scala
  import java.awt.Font
  
  val fontStream = getClass.getResourceAsStream("/font/DejaVu-Sans.ttf")
  val dejaVuSans = Font.createFont(Font.TRUETYPE_FONT, fontStream)
  ```
  
3. Register the font with the graphics environment to make it available to the operating system.

  ```scala
  import java.awt.GraphicsEnvironment
  
  GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(dejaVuSans)
  ```
  
Now you can use a @:api(doodle.core.font.FontFamily) with the name `"DejaVu Sans"` and it will use the font you just registered. 

```scala mdoc:silent
import doodle.core.font.*

Font(FontFamily.named("DejaVu Sans"))
```

Note that this name is determined by information stored in the font file, so adjust is appropriately for the fonts you are using.

[dejavu-sans]: https://dejavu-fonts.github.io/
