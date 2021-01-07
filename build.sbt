//   https://github.com/awwsmm/IntroToScalaTags

name := "IntroToScalaTags"
version := "0.1"
scalaVersion := "2.13.3"

// This is a ScalaJS project
enablePlugins(ScalaJSPlugin)

// This is an application with a main method
scalaJSUseMainModuleInitializer := true

// Start by running Index.main()
mainClass in Compile := Some("scalatags.Index")
