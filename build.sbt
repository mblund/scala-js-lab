enablePlugins(ScalaJSPlugin)

name := "Example"

version := "0.1-SNAPSHOT"

scalaVersion := "2.11.5"

persistLauncher := true

persistLauncher in Test := false

libraryDependencies ++= Seq(
  "org.scala-js" %%% "scalajs-dom" % "0.8.0",
  "com.lihaoyi" %%% "scalatags" % "0.4.5",
  "com.lihaoyi" %%% "utest" % "0.3.0" % Test,
  "be.doeraene" %%% "scalajs-jquery" % "0.8.0"
)

testFrameworks += new TestFramework("utest.runner.Framework")

jsDependencies += RuntimeDOM

requiresDOM := true

scalaJSStage in Global := FastOptStage

relativeSourceMaps := true





