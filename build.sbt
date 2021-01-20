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

// Start by running Index.main()
mainClass in Compile := Some("scalatags.Index")
