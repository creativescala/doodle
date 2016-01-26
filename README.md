# Doodle: Compositional Graphics

Copyright 2015 [Underscore](http://underscore.io).

A Scala library for compositional vector graphics,
with a native back-end via Swing and Java2D,
and an HTML canvas backend via [Scala.js](http://www.scala-js.org/).

Distributed under the [Apache 2.0](http://www.apache.org/licenses/LICENSE-2.0.txt) license.

[![Build Status](https://travis-ci.org/underscoreio/doodle.svg?branch=develop)](https://travis-ci.org/underscoreio/doodle)
[![Join the chat at https://gitter.im/underscoreio/doodle](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/underscoreio/doodle?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

# Creative Scala

Doodle is featured in [Creative Scala][creative-scala],
our free introductory Scala ebook.
Follow the link to download your copy from our web site.

[creative-scala]: http://underscore.io/training/courses/creative-scala

# Getting Started

1.  Start SBT:

    ~~~ bash
    bash$ sbt

    > # This is the SBT prompt. Press Ctrl+D to quit to the OS.
    ~~~

2.  Start the console:

    ~~~ coffee
    > console

    scala> # This is the Scala prompt. Press Ctrl+D to quit to SBT.
    ~~~

3.  Use Scala commands to draw a shape in a native window:

    ~~~ scala
    scala> (Circle(10) fillColor Color.red).draw
    ~~~

    A window should appear containing a red circle.

# Drawing in the Browser

You can also draw Doodle pictures in the browser. The process is slightly different.

1.  Write your code in `js/src/main/scala/Main.scala`

2.  Start SBT:

    ~~~ bash
    bash$ sbt

    > # This is the SBT prompt. Press Ctrl+D to quit to the OS.
    ~~~

3.  Compile the JS version:

    ~~~ scala
    > fastOptJS
    ~~~

4.  Open your browser and point to `http://localhost:12345/index.html`
    to see an HTML 5 canvas version of the code.

# Viewing the Examples

Doodle ships with a set of examples for each of the exercises in Creative Scala.
See the `shared/src/main/scala/doodle/examples` directory for a complete list.

To view an example, simply run the sbt console, and draw the examples from there. For example:

~~~ bash
bash$ sbt console
~~~

When the console is running, type

~~~ coffee
scala> Sierpinski.image.draw
~~~

# Directory Structure

Source code is in the following directories:

~~~ coffee
 - shared / src / {main,test} / scala
 - jvm    / src / {main,test} / scala
 - js     / src / {main,test} / scala
~~~

# SBT Commands

We provide three quick aliases for common commands:

~~~ coffee
> console   runs the console using the JVM codebase
> fastOptJS compiles the JS codebase
> test      runs the unit tests for both codebases
~~~

# Using Doodle as a Library

You can grab Doodle from our *training* repository on Bintray.
See [the releases tab](https://github.com/underscoreio/doodle/releases) for a list of releases.
Once you know which release you want, add the following to your `build.sbt`:

~~~ scala
scalaVersion := "2.11.2" // Doodle is compatible with Scala 2.11 only

resolvers += "Underscore Training" at "https://dl.bintray.com/underscoreio/training"

libraryDependencies += "underscoreio" %% "doodle" % <<RELEASE_VERSION>>
~~~

If you are considering using Doodle on the Scala console,
you may also want to add the following to your build:

~~~ scala
initialCommands in console := """
  |import doodle.core._
  |import doodle.syntax._
  |import doodle.jvm._
  |import doodle.examples._
""".trim.stripMargin
~~~

[bintray-training]: https://bintray.com/underscoreio/training
[doodle-releases]: https://bintray.com/underscoreio/training/doodle/view

# Acknowledgements

Doodle was written by Noel Welsh and Dave Gurnell with contributions from Jono Ferguson, Richard Dallaway, and Mat Moore.
