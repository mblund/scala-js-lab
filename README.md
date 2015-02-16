# Example application written in Scala.js

This is another to-do list application. This time written in [Scala.js](https://www.scala-js.org/) and
[Scalatags](https://github.com/lihaoyi/scalatags) as html-templates.

The application is inspired of event sourcing to create undo and redo functionality together with the scala standard
library.

There is a working online version [here](http://www.magnusberglund.com/scala-js-lab/)


## Get started

To get started, open `sbt` in this example project, and execute the task
`fastOptJS`. This creates the file `target/scala-2.11/example-fastopt.js`.
You can now open `index-fastopt.html` in your favorite Web browser!

During development, it is useful to use `~fastOptJS` in sbt, so that each
time you save a source file, a compilation of the project is triggered.
Hence only a refresh of your Web page is needed to see the effects of your
changes.

## Run the tests

To run the test suite, you have to install [Phantom.js](http://phantomjs.org/)

"""
PhantomJS is a headless WebKit.... It has fast and native support for various web standards: DOM handling, CSS selector, JSON, Canvas, and SVG.
"""

I used Brew to install it with brew install phantomjs. But there is also a [download page](http://phantomjs.org/download.html)
After you have installed phantom you can launch sbt and run `test`


## The fully optimized version

For ultimate code size reduction, use `fullOptJS`. This will take several
seconds to execute, so typically you only use this for the final, production
version of your application. While `index-fastopt.html` refers to the
JavaScript emitted by `fastOptJS`, `index.html` refers to the optimized
JavaScript emitted by `fullOptJS`.
