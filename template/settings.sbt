inThisBuild(
  Seq(
    organization := "org.scalablytyped",
    scalaVersion := "2.13.0",
    publishArtifact in packageDoc := false,
    scalacOptions ++= List("-P:scalajs:sjsDefinedByDefault", "-g:notailcalls"),
    licenses += ("MIT", url("http://opensource.org/licenses/MIT")),
    resolvers += "jitpack" at "https://jitpack.io",
  )
)
