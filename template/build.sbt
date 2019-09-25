lazy val `scalably-typed` = project
  .in(file("."))
  .aggregate(
    std,
    node,
    `keycloak-js`,
    eventsource,
    `rsocket-flowable`,
    `rsocket-types`,
    `rsocket-core`,
    `rsocket-websocket-client`
  )

lazy val std = project
  .enablePlugins(ScalaJSPlugin)
  .settings(
    libraryDependencies ++= Seq(
      "com.olvind" %%% "scalablytyped-runtime" % "2.1.0"
    )
  )

lazy val node = project
  .enablePlugins(ScalaJSPlugin)
  .dependsOn(std)

lazy val `keycloak-js` = project
  .enablePlugins(ScalaJSPlugin)
  .dependsOn(std)

lazy val eventsource = project
  .enablePlugins(ScalaJSPlugin)
  .dependsOn(std)

lazy val `rsocket-flowable` = project
  .enablePlugins(ScalaJSPlugin)
  .dependsOn(std, node)

lazy val `rsocket-types` = project
  .enablePlugins(ScalaJSPlugin)
  .dependsOn(`rsocket-flowable`)

lazy val `rsocket-core` = project
  .enablePlugins(ScalaJSPlugin)
  .dependsOn(`rsocket-types`)

lazy val `rsocket-websocket-client` = project
  .enablePlugins(ScalaJSPlugin)
  .dependsOn(`rsocket-core`)
