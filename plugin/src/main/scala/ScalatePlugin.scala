package com.mojolly.scalate

import sbt._
import Keys._
import Project.Initialize
import java.io.File
import sbt.classpath.ClasspathUtilities

object ScalatePlugin extends Plugin {

  case class Binding(
    name: String,
    className: String = "Any",
    importMembers: Boolean = false)

  val Scalate = config("scalate") hide

  val scalateTemplateDirectory = SettingKey[File]("scalate-template-directory",
    "Locations of template files.")

  val scalateLoggingConfig = SettingKey[File]("scalate-logging-config",
    "Logback config to get rid of that infernal debug output.")

  val scalateImports = SettingKey[Seq[String]]("scalate-imports",
    "The import statements for Scalate templates")

  val scalateBindings = SettingKey[Seq[Binding]]("scalate-bindings",
    "The bindings for Scalate templates")
    
  val scalateOverwrite = SettingKey[Boolean]("scalate-overwrite",
    "Always generate the Scala sources even when they haven't changed")
    
  private def scalateLoggingConfigValue: Initialize[File] =
    (resourceDirectory in Compile) { (d) => new File(d, "/logback.xml") }

  def scalateTemplateDirectoryValue: Initialize[File] =
    (resourceDirectory in Compile) { (d) => d }

  def scalateSourceGeneratorTask: Initialize[Task[Seq[File]]] = {
    (streams, sourceManaged in Compile, scalateTemplateDirectory in Compile, scalateLoggingConfig in Compile, managedClasspath in scalateClasspaths, scalateImports in Compile, scalateBindings in Compile, scalateOverwrite in Compile) map {
      (out, outputDir, inputDirs, logConfig, cp, imports, bindings, overwrite) => generateScalateSource(out, new File(outputDir, "scalate"), inputDirs, logConfig, cp, imports, bindings, overwrite)
    }
  }

  val scalateClasspaths = TaskKey[ScalateClasspaths]("scalate-classpaths")
  final case class ScalateClasspaths(classpath: PathFinder, scalateClasspath: PathFinder)

  def scalateClasspathsTask(cp: Classpath, scalateCp: Classpath) = ScalateClasspaths(cp.map(_.data), scalateCp.map(_.data))

  def generateScalateSource(out: TaskStreams, outputDir: File, inputDir: File, logConfig: File, cp: Classpath, imports: Seq[String], bindings: Seq[Binding], overwrite: Boolean) = {
    withScalateClassLoader(cp.files) { classLoader =>
      type Generator = {
        var sources: File
        var targetDirectory: File
        var logConfig: File
        var overwrite: Boolean
        var scalateImports: Array[String]
        var scalateBindings: Array[Array[AnyRef]]
        def execute: Array[File]
      }

      val className = "com.mojolly.scalate.Generator"
      val klass = classLoader.loadClass(className)
      val inst = klass.newInstance
      val generator = klass.newInstance.asInstanceOf[Generator]

      generator.sources = inputDir
      generator.targetDirectory = outputDir
      generator.logConfig = logConfig
      generator.overwrite = overwrite
      generator.scalateImports = imports.toArray
      generator.scalateBindings = bindings.toArray map { b =>
        Array(b.name.asInstanceOf[AnyRef], b.className.asInstanceOf[AnyRef], b.importMembers.asInstanceOf[AnyRef])
      }
      generator.execute.toList
    }
  }

  val scalateSettings: Seq[sbt.Project.Setting[_]] = Seq(
    ivyConfigurations += Scalate,
    scalateLoggingConfig in Compile <<= (resourceDirectory in Compile) { _ / "logback.xml" },
    scalateTemplateDirectory in Compile <<= (resourceDirectory in Compile),
    libraryDependencies += "com.mojolly.scalate" %% "scalate-generator" % Version.version % Scalate.name,
    sourceGenerators in Compile <+= scalateSourceGeneratorTask,
    scalateOverwrite := true,
    managedClasspath in scalateClasspaths <<= (classpathTypes, update) map { ( ct, report)   =>
	  Classpaths.managedJars(Scalate, ct, report)
	},
    scalateClasspaths <<= (fullClasspath in Runtime, managedClasspath in scalateClasspaths) map scalateClasspathsTask,
    scalateBindings := Nil,
    scalateImports := Nil)

  /**
   * Runs a block of code with the Scalate classpath as the context class
   * loader.  The Scalate classpath is the [[runClassPath]] plus the
   * [[buildScalaInstance]]'s jars.
   */
  protected def withScalateClassLoader[A](runClassPath: Seq[File])(f: ClassLoader => A): A = {
    val oldLoader = Thread.currentThread.getContextClassLoader
    val loader = ClasspathUtilities.toLoader(runClassPath)
    Thread.currentThread.setContextClassLoader(loader)
    try {
      f(loader)
    } finally {
      Thread.currentThread.setContextClassLoader(oldLoader)
    }
  }

}
