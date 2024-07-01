# Development Process


## Automated Code Checks

The `build` task in sbt should be run whenever you have finished code that is ready to be pushed to the main repository. As well as compiling code and running tests this command formats code, organizes imports, checks dependencies are up to date, and more. Many of these are checked by CI, and your code will be rejected if the checks do not pass.


## Dependencies

We are generally fast to update dependencies, so don't be afraid to add a commit that updates old dependencies if you see any. The `build` command will tell you if any new versions of dependencies are available.


## Testing

Testing is difficult for a project like Doodle, where the output can be hard to capture and automatically assess. Thus we take a layered approach to testing. Small and fast unit tests (usually generative) are great, but to check that output is rendering correctly we have a few other options:

* In the `golden` project we have tests that compare rendered pictures to known good output. This runs on the JVM, and therefore only checks the Java2D output (and the generic algebras on which it depends).

* Our documentation serves as a form of testing for pictures that render in the browser. There are backend independent implementation of many examples in the `examples` project.


## Documentation

New functionality isn't finished until documentation is written. 
