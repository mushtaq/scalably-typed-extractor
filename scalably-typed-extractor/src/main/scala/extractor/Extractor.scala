package extractor

import ammonite.ops._
import coursier._
import coursier.util.Task.sync
import sbt.Logger
import sbt.librarymanagement.ModuleID
import txt.SbtFileContent

class Extractor(stRepo: Path, myRepo: Path, modules: Seq[ModuleID], log: Logger) {

  lazy val resolution: Resolution = Resolve()
    .addDependencies(modules.map(dependency): _*)
    .addRepositories(Repositories.bintray("oyvindberg", "ScalablyTyped"))
    .run()

  lazy val results: Map[String, Seq[String]] = resolution.finalDependenciesCache.collect {
    case (p, deps) if name(p) != "std" => name(p) -> deps.map(name)
  }

  results.foreach(x => log.info(x.toString()))

  rm ! myRepo / "modules"
  rm ! myRepo / "modules.sbt"

  write(myRepo / "modules.sbt", SbtFileContent(myRepo.baseName, results).body)

  private val projectNames = results.keySet ++ Set("std")

  projectNames.foreach { p =>
    log.info(s"************* creating $p")
    val modulePath = myRepo / "modules" / p
    mkdir ! modulePath

    val stModule = {
      if (p.endsWith("-facade")) {
        stRepo / "facades" / p.stripSuffix("-facade")
      } else {
        stRepo / p.head.toString / p
      }
    }

    cp.into(stModule / "src", modulePath)
    cp.into(stModule / "readme.md", modulePath)
  }

  log.info("Done--------------------------------------------")

  def name(x: Dependency): String = x.module.name.value.stripSuffix("_sjs0.6_2.13")

  lazy val exclusions: Set[(Organization, ModuleName)] = Set(
    org"com.olvind"     -> name"scalablytyped-runtime_sjs0.6_2.13",
    org"org.scala-js"   -> name"scalajs-library_2.13",
    org"org.scala-lang" -> name"scala-library"
  )

  def dependency(moduleID: ModuleID): Dependency = {
    val module = Module(
      Organization(moduleID.organization),
      ModuleName(moduleID.name)
    )
    Dependency.of(module, moduleID.revision).withExclusions(exclusions)
  }

}
