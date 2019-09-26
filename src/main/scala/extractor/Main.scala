package extractor

import ammonite.ops._
import coursier._
import coursier.util.Task.sync
import txt.SbtFileContent

object Main {

  def main(args: Array[String]): Unit = {

    val stRepo = Path(args(0))
    val myRepo = Path(args(1))

    val modules: Seq[String] = read(myRepo / "project-list.txt").linesIterator.toList

    val resolution: Resolution = Resolve()
      .addDependencies(modules.map(dependency): _*)
      .addRepositories(Repositories.bintray("oyvindberg", "ScalablyTyped"))
      .run()

    val results: Map[String, Seq[String]] = resolution.finalDependenciesCache.collect {
      case (p, deps) if name(p) != "std" => name(p) -> deps.map(name)
    }

    results.foreach(println)
    createProject(stRepo, myRepo, results)
  }

  def createProject(stRepo: Path, myRepo: Path, results: Map[String, Seq[String]]): Unit = {
    rm ! myRepo / "modules"
    rm ! myRepo / "build.sbt"

    write(myRepo / "build.sbt", SbtFileContent(myRepo.baseName, results).body)

    val projectNames = results.keySet ++ Set("std")

    projectNames.foreach { p =>
      val modulePath = myRepo / "modules" / p
      mkdir ! modulePath

      val stModule = stRepo / p.head.toString / p
      cp.into(stModule / "src", modulePath)
      cp.into(stModule / "readme.md", modulePath)
    }

  }

  def name(x: Dependency): String = x.module.name.value.stripSuffix(ScalaJsSuffix)

  val exclusions: Set[(Organization, ModuleName)] = Set(
    org"com.olvind"     -> name"scalablytyped-runtime_sjs0.6_2.12",
    org"org.scala-js"   -> name"scalajs-library_2.12",
    org"org.scala-lang" -> name"scala-library"
  )

  val ScalaJsSuffix = "_sjs0.6_2.12"

  def dependency(name: String): Dependency = {
    val module = Module(
      org"org.scalablytyped",
      ModuleName(s"$name$ScalaJsSuffix")
    )
    Dependency.of(module, "[,]").withExclusions(exclusions)
  }

}
