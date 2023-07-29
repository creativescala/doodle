# Doodle: Compositional Vector Graphics

Copyright [Noel Welsh](http://noelwelsh.com).

Doodle is a Scala library for compositional vector graphics.

Distributed under the [Apache 2.0](http://www.apache.org/licenses/LICENSE-2.0.txt) license.


## Using Doodle

![Current Version](https://img.shields.io/maven-central/v/org.creativescala/doodle_3)

Doodle is a Scala 3 only project. The current release is shown above and is on the `main` branch.

To use doodle add the following to your `build.sbt`:

~~~ scala
// Doodle is currently published for Scala 3
libraryDependencies += "org.creativescala" %% "doodle" % "<version>"
~~~

replacing `<version>` with the latest version as shown above. (Be aware you only need the numeric part of the version. So use a string like `"0.20.0"` not `"v0.20.0"`).

Scala 2.13 users should use version 0.19.0.


## Documentation

[Documentation](https://creativescala.github.io/doodle/) is available on the microsite.

[Creative Scala][creativescala] provides another source of documentation for Doodle. Creative Scala is a free introductory Scala ebook. 

[creativescala]: http://creativescala.org/

## Acknowledgements

Doodle was written by Noel Welsh with contributions from [the contributors listed by Github][github-contributors].

[github-contributors]: https://github.com/creativescala/doodle/graphs/contributors


## Notes

These are notes for developers.

### General Development

- Use the `build` task in sbt to compile and test everything, and run formatting.
- Use the `prePR` task for additional checks before submitting a PR.


### Documentation

Thanks to the *amazing* Typelevel SBT plugin, documentation should be built on every push to the master branch.

### Publishing

Push a tag of the form `v0.14.0`. Make sure `tlBaseVersion` matches the base version you're trying to release.
