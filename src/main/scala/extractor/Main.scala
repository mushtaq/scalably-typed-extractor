package extractor

import coursier._
import coursier.util.Task.sync

object Main {

  val modules: Seq[String] = Seq(
    "react-dom",
    "rsocket-websocket-client"
  )

  def main(args: Array[String]): Unit = {
    val resolution: Resolution = Resolve()
      .addDependencies(modules.map(dependency): _*)
      .addRepositories(Repositories.bintray("oyvindberg", "ScalablyTyped"))
      .run()

    val results: Map[String, Seq[String]] = resolution.finalDependenciesCache.map {
      case (x, xs) => name(x) -> xs.map(name)
    }

    results.foreach(println)
  }

  private def name(x: Dependency): String = x.module.name.value.stripSuffix(ScalaJsSuffix)

  val exclusions: Set[(Organization, ModuleName)] = Set(
    org"com.olvind"     -> name"scalablytyped-runtime_sjs0.6_2.12",
    org"org.scala-js"   -> name"scalajs-library_2.12",
    org"org.scala-lang" -> name"scala-library"
  )

  val ScalaJsSuffix = "_sjs0.6_2.12"

  def dependency(name: String): Dependency =
    Dependency
      .of(
        Module(
          org"org.scalablytyped",
          ModuleName(s"$name$ScalaJsSuffix")
        ),
        "[,]"
      )
      .withExclusions(exclusions)
}
