package extractor

import java.nio.file.Files

import coursier._
import coursier.util.Task.sync
import txt.build
import ammonite.ops._

object Main {

  def main(args: Array[String]): Unit = {

    val modules: Seq[String] = read(pwd / "project-list.txt").linesIterator.toList

    val resolution: Resolution = Resolve()
      .addDependencies(modules.map(dependency): _*)
      .addRepositories(Repositories.bintray("oyvindberg", "ScalablyTyped"))
      .run()

    val results: Map[String, Seq[String]] = resolution.finalDependenciesCache.map {
      case (x, xs) => name(x) -> xs.map(name)
    }

    println(build("tmt-typed", results).body)

    println(ls ! pwd / "template")

    println(Path(args(0)))

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
