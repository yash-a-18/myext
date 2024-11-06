import scala.sys.process._

lazy val installDependencies = Def.task[Unit] {
  val base = (ThisProject / baseDirectory).value
  val log = (ThisProject / streams).value.log
  if (!(base / "node_module").exists) {
    val pb =
      new java.lang.ProcessBuilder("npm.cmd", "install")
        .directory(base)
        .redirectErrorStream(true)

    pb ! log
  }
}

lazy val open = taskKey[Unit]("open vscode")
def openVSCodeTask: Def.Initialize[Task[Unit]] =
  Def
    .task[Unit] {
      val base = baseDirectory.value
      val log = streams.value.log

      val path = base.getCanonicalPath
      s"code.cmd --extensionDevelopmentPath=$path" ! log
      ()
    }
    // .dependsOn(installDependencies)

lazy val root = project
  .in(file("."))
  .settings(
    scalacOptions ++=  Seq("-Yretain-trees",//necessary in zio-json if any case classes have default parameters
    "-Xmax-inlines","60"), //setting max inlines to accomodate > 32 fields in case classes
    scalaVersion := DependencyVersions.scala,
    moduleName := "myext",
    Compile / fastOptJS / artifactPath := baseDirectory.value / "out" / "extension.js",
    Compile / fullOptJS / artifactPath := baseDirectory.value / "out" / "extension.js",
    open := openVSCodeTask.dependsOn(Compile / fastOptJS).value,
        // CommonJS
    scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.CommonJSModule) },

    // Compile / npmDependencies ++= Seq("@types/vscode" -> "1.84.1"),
    // Tell ScalablyTyped that we manage `npm install` ourselves
    externalNpm := baseDirectory.value,
    libraryDependencies += "com.raquo" %%% "laminar" % "17.1.0",
    libraryDependencies += "io.laminext" %%% "fetch" % "0.17.0",
    libraryDependencies += "dev.zio" %% "zio-json" % "0.6.0",
    libraryDependencies += "org.typelevel" %%% "shapeless3-deriving" % "3.4.3",
    libraryDependencies += "com.axiom" %%% "dataimportcsv3s" % "0.0.1-SNAPSHOT",
    libraryDependencies += "io.bullet" %%% "borer-core" % "1.14.0",
    libraryDependencies += "io.bullet" %%% "borer-derivation" % "1.14.0",
    testFrameworks += new TestFramework("utest.runner.Framework")
    // publishMarketplace := publishMarketplaceTask.dependsOn(fullOptJS in Compile).value
  )
  .enablePlugins(
    ScalaJSPlugin,
    ScalablyTypedConverterExternalNpmPlugin
    // ScalablyTypedConverterPlugin
  )
