# Compositional Graphics

by [Underscore](http://underscore.io).

A Scala library for compositional vector graphics,
with a native back-end via Swing and Java2D,
and an HTML canvas backend via [Scala.js](http://www.scala-js.org/).

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
    scala> draw(Circle(10) fillColor Color.rgba(0, 0, 0, 0))
    ~~~

    A window should appear containing a black circle.

4.  Return to the SBT prompt and compile the JS version:

    ~~~ scala
    > fastOptJS
    ~~~

5.  Open your browser and point to `http://localhost:12345`
    to see an HTML 5 canvas version of the code.

# Directory Structure

Source code is in the following directories:

~~~ coffee
 - shared / src / {main,test} / scala
 - jvm    / src / {main,test} / scala
 - js     / src / {main,test} / scala
~~~

The main file for editing is:

~~~ coffee
shared/src/main/scala/doodle/Example.scala
~~~

# SBT Commands

We provide four quick aliases for common commands:

~~~ coffee
> run       runs the JVM codebase
> console   runs the console using the JVM codebase
> fastOptJS compiles the JS codebase
> test      runs the unit tests for both codebases
~~~
