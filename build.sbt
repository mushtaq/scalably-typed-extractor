name := "scalably-typed-extractor"
organization := "com.github.tmtsoftware.scalably-typed-extractor"
version := "0.0.1-SNAPSHOT"
scalaVersion := "2.12.10"

sbtPlugin := true

enablePlugins(SbtTwirl, ScriptedPlugin)

libraryDependencies ++= Seq(
  "com.lihaoyi"     % "ammonite"        % "1.7.1" cross CrossVersion.full,
  "io.get-coursier" %% "coursier"       % "2.0.0-RC3-4",
  "io.get-coursier" %% "coursier-cache" % "2.0.0-RC3-4"
)

scriptedLaunchOpts += ("-Dplugin.version=" + version.value)
scriptedLaunchOpts ++= sys.process.javaVmArguments.filter(
  a => Seq("-Xmx", "-Xms", "-XX", "-Dsbt.log.noformat").exists(a.startsWith)
)

scriptedBufferLog := false
