package extractor

import sbt.Keys._
import sbt._

object ExtractorPlugin extends AutoPlugin {
  override val trigger: PluginTrigger = noTrigger

  override val requires: Plugins = plugins.JvmPlugin

  object autoImport {
    lazy val stRepo = settingKey[File]("location of the cloned and freshly updated ScalablyTyped repo")
    lazy val stModules = settingKey[Seq[ModuleID]](
      "list of ScalablyTyped modules, ideally selected via the BoM provided by ScalablytypedPlugin"
    )
    lazy val extract = taskKey[Unit]("Extracts the sources from the listed modules")
  }

  import autoImport._

  override lazy val projectSettings: Seq[Setting[_]] = Seq(
    stRepo := file("/tmp"),
    stModules := Nil,
    extract := extractTask.value
  )

  private def extractTask = Def.task {
    new Extractor(ammonite.ops.Path(stRepo.value), ammonite.ops.Path(baseDirectory.value), stModules.value, sLog.value)
  }
}
