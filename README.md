# jenkins-library

### Use libraries template
[Link to Docs!](libraries/README.adoc)

### Unit Test
Libraries leverage [Jenkins Spock](https://github.com/ExpediaGroup/jenkins-spock) as a unit testing framework for testing the library steps.

Each library that has unit tests has a subdirectory under test containing the individual Specification files for the library.

To execute the unit tests, run make test.
