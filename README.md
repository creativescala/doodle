# Doodle: Compositional Vector Graphics

Copyright [Noel Welsh](http://noelwelsh.com).

Doodle is a Scala library for compositional vector graphics.

Distributed under the [Apache 2.0](http://www.apache.org/licenses/LICENSE-2.0.txt) license.

[![Build Status](https://travis-ci.org/creative-scala/doodle.svg?branch=develop)](https://travis-ci.org/creative-scala/doodle)


# Using Doodle

This is the development branch of Doodle. The current release is **`0.9.0-SNAPSHOT`**. The `master` branch has the released version.

You can use Doodle in your own project by importing the JARs published to Maven Central. To use it add the following to your `build.sbt`:

~~~ scala
scalaVersion := "2.12.8" // Doodle is currently published for Scala 2.12 only
libraryDependencies += "org.creativescala" %% "doodle" % "0.9.0"
~~~

Alternatively you can `git clone` or download Doodle and use it directly from the SBT console. See the instructions below.


# Documentation

[Documentation](https://www.creativescala.org/doodle/) is still a work-in-progress.

[Creative Scala][creativescala] provides another source of documentation for Doodle. Creative Scala is a free introductory Scala ebook. 

[creativescala]: http://creativescala.org/

Below we have a few tips to get you started.

## Getting Started from SBT

If you downloaded Doodle, rather than adding it to an existing Scala project, you can play around with it as follows.

1.  Start SBT:

    ~~~ bash
    bash$ sbt

    > # This is the SBT prompt. Press Ctrl+D to quit to the OS.
    ~~~

2.  Start the console:

    ~~~ coffee
    > rootJVM/console

    scala> # This is the Scala prompt. Press Ctrl+D to quit to SBT.
    ~~~

3.  Use Scala commands to draw a shape in a native window:

    ~~~ scala
    scala> (Image.circle(10).fillColor(Color.red)).draw()
    ~~~

    A window should appear containing a red circle.
    
4.  You can also save your masterpieces to a file.

    ~~~ scala
    scala> (Image.circle(10).fillColor.(Color.red)).write[Png]("masterpiece.png")
    ~~~
    
    Doodle currently supports writing to PNG, GIF, and JPG formats. Just alter the type parameter of `write` accordingly.


# Acknowledgements

Doodle was written by Noel Welsh with contributions from [the contributors listed by Github][github-contributors].

[github-contributors]: https://github.com/creative-scala/doodle/graphs/contributors
