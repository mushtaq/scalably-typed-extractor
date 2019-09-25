package extractor

import ammonite.ops._
import coursier._
import coursier.util.Task.sync
import txt.SbtFileContent

object Main {

  def main(args: Array[String]): Unit = {

    val scalablyTypedPath = Path(args(0))
    val targetPath        = Path(args(1))

    val modules: Seq[String] = read(pwd / "project-list.txt").linesIterator.toList

    val resolution: Resolution = Resolve()
      .addDependencies(modules.map(dependency): _*)
      .addRepositories(Repositories.bintray("oyvindberg", "ScalablyTyped"))
      .run()

    val results: Map[String, Seq[String]] = resolution.finalDependenciesCache.collect {
      case (p, deps) if name(p) != "std" => name(p) -> deps.map(name)
    }

    results.foreach(println)
    createProject(scalablyTypedPath, targetPath, results)
  }

  def createProject(scalablyTypedPath: Path, targetPath: Path, results: Map[String, Seq[String]]): Unit = {
    val templatePath = targetPath / "template"
    rm ! templatePath

    cp.into(pwd / "template", targetPath)

    write(templatePath / "build.sbt", SbtFileContent("tmt-typed", results).body)

    val projectNames = results.keySet ++ Set("std")

    projectNames.foreach { p =>
      val targetProjectPath = templatePath / p
      mkdir ! targetProjectPath

      val sourceProjectPath = scalablyTypedPath / p.head.toString / p
      cp.into(sourceProjectPath / "src", targetProjectPath)
      cp.into(sourceProjectPath / "readme.md", targetProjectPath)
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
