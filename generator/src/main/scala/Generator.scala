package com.mojolly.scalate

import org.fusesource.scalate.{ TemplateEngine, TemplateSource, Binding }
import org.fusesource.scalate.util.IOUtil

import java.io.File

/**
 * Uses the Scalate template engine to generate Scala source files for Scalate templates.
 */
class Generator {

  var sources: File = _
  var targetDirectory: File = _
  var logConfig: File = _
  var overwrite: Boolean = _
  var scalateImports: Array[String] = Array.empty
  var scalateBindings: Array[Array[AnyRef]] = Array.empty // weird structure to represent Scalate Binding
  var packagePrefix: String = _

  lazy val engine = {
    val e = new TemplateEngine

    // initialize template engine
    e.importStatements = scalateImports.toList
    e.bindings = (scalateBindings.toList map { b =>
      Binding(
        b(0).asInstanceOf[String],
        b(1).asInstanceOf[String],
        b(2).asInstanceOf[Boolean],
        (b(3) match {
          case null | "" => None
          case a => Some(a.toString)
        }),
        b(4).asInstanceOf[String],
        b(5).asInstanceOf[Boolean])
    }) ::: e.bindings
    e
  }

  def execute: Array[File] = {

    System.setProperty("logback.configurationFile", logConfig.toString)

    if (sources == null) {
      throw new IllegalArgumentException("The sources property is not properly set")
    }
    if (targetDirectory == null) {
      throw new IllegalArgumentException("The targetDirectory property is not properly set")
    }

    engine.packagePrefix = packagePrefix

    targetDirectory.mkdirs

    var paths = List.empty[String]
    for (extension <- engine.codeGenerators.keysIterator) {
      paths = collectUrisWithExtension(sources, "", "." + extension) ::: paths
    }

    paths collect {
      case Updated(uri, templateFile, scalaFile) =>
        val template = TemplateSource.fromFile(templateFile, uri)

        val code = engine.generateScala(template).source
        scalaFile.getParentFile.mkdirs
        IOUtil.writeBinaryFile(scalaFile, code.getBytes("UTF-8"))
        scalaFile
    } toArray
  }

  object Updated {
    def unapply(uri: String) = {
      val templateFile = new File(sources, uri)
      val scalaFile = new File(targetDirectory, "/%s.scala".format(uri.replaceAll("[.]", "_")))
      if (overwrite || !scalaFile.exists || templateFile.lastModified > scalaFile.lastModified) Some(uri, templateFile, scalaFile) else None
    }
  }

  protected def collectUrisWithExtension(basedir: File, baseuri: String, extension: String): List[String] = {
    var collected = List[String]()
    if (basedir.isDirectory()) {
      var files = basedir.listFiles();
      if (files != null) {
        for (file <- files) {
          if (file.isDirectory()) {
            collected = collectUrisWithExtension(file, baseuri + "/" + file.getName(), extension) ::: collected;
          } else {
            if (file.getName().endsWith(extension)) {
              collected = baseuri + "/" + file.getName() :: collected
            } else {
            }

          }
        }
      }
    }
    collected
  }

}