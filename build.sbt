name := "scalably-typed-extraction"

version := "0.1"

scalaVersion := "2.13.0"

libraryDependencies ++= Seq(
  "com.lihaoyi" % "ammonite" % "1.7.1" cross CrossVersion.full,
  "io.get-coursier" %% "coursier" % "2.0.0-RC3-4",
  "io.get-coursier" %% "coursier-cache" % "2.0.0-RC3-4"
)
