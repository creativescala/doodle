# Doodle: Compositional Vector Graphics

Copyright [Noel Welsh](http://noelwelsh.com).

Doodle is a Scala library for compositional vector graphics.

Distributed under the [Apache 2.0](http://www.apache.org/licenses/LICENSE-2.0.txt) license.

[![Build Status](https://travis-ci.org/creativescala/doodle.svg?branch=develop)](https://travis-ci.org/creativescala/doodle)


## Using Doodle

The current release is **0.18.0** and is on the `main` branch.

To use doodle add the following to your `build.sbt`:

~~~ scala
// Doodle is currently published for Scala 2.13 and Scala 3
libraryDependencies += "org.creativescala" %% "doodle" % "0.18.0"
~~~

## Documentation

[Documentation](https://creativescala.github.io/doodle/) is available on the microsite.

[Creative Scala][creativescala] provides another source of documentation for Doodle. Creative Scala is a free introductory Scala ebook. 

[creativescala]: http://creativescala.org/

## Acknowledgements

Doodle was written by Noel Welsh with contributions from [the contributors listed by Github][github-contributors].

[github-contributors]: https://github.com/creativescala/doodle/graphs/contributors


## Notes

These are notes for developers.

### Documentation

Thanks to the *amazing* Typelevel SBT plugin, documentation should be built on every push to the master branch.

### Publishing

Push a tag of the form `v0.14.0`. Make sure `tlBaseVersion` matches the base version you're trying to release.
