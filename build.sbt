lazy val `scalably-typed-extractor-root` = project
  .in(file("."))
  .aggregate(`scalably-typed-extractor`)

lazy val `scalably-typed-extractor` = project
  .enablePlugins(SbtTwirl, ScriptedPlugin)
  .settings(
    organization := "com.github.tmtsoftware.scalably-typed-extractor",
    version := "0.1.3-SNAPSHOT",
    scalaVersion := "2.12.10",
    sbtPlugin := true,
    libraryDependencies ++= Seq(
      "com.lihaoyi" % "ammonite" % "1.7.1" cross CrossVersion.full,
      "io.get-coursier" %% "coursier" % "2.0.0-RC3-4",
      "io.get-coursier" %% "coursier-cache" % "2.0.0-RC3-4"
    ),
    scriptedLaunchOpts += ("-Dplugin.version=" + version.value),
    scriptedLaunchOpts ++= sys.process.javaVmArguments.filter(
      a => Seq("-Xmx", "-Xms", "-XX", "-Dsbt.log.noformat").exists(a.startsWith)
    ),
    scriptedBufferLog := false,
    publishMavenStyle := true,
    licenses := Seq(
      ("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0"))
    ),
    bintrayRepository := "sbt-plugins",
    bintrayOrganization in bintray := None
  )
