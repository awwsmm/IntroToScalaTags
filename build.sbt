//   https://github.com/awwsmm/IntroToScalaTags

name := "IntroToScalaTags"
version := "0.1"
scalaVersion := "2.13.3"

// This is a ScalaJS project
enablePlugins(ScalaJSPlugin)

// This is an application with a main method
scalaJSUseMainModuleInitializer := true

// ScalaTags
libraryDependencies += "com.lihaoyi" %%% "scalatags" % "0.9.2"

// sttp
libraryDependencies += "com.softwaremill.sttp.client3" %% "core" % "3.0.0"

// circe
val circeVersion = "0.12.3"

libraryDependencies ++= Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-parser"
).map(_ % circeVersion)

// Start by running Index.main()
mainClass in Compile := Some("scalatags.Index")
