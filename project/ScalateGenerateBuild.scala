import sbt._
import Keys._

object ScalateGenerateBuild extends Build {
    
  val buildSettings = Defaults.defaultSettings ++ Seq(
    version := "0.0.1-SNAPSHOT",
    organization := "com.mojolly.scalate"
  )

  lazy val root = Project("xsbt-scalate", file("."), settings = buildSettings) aggregate (generator, plugin)

  lazy val generator = Project(
    "scalate-generator",
    file("generator"),
    settings = buildSettings ++ Seq(
      scalaVersion := "2.9.0-1",
      libraryDependencies += "org.fusesource.scalate" % "scalate-core" % "1.5.0" % "compile"
    )
  )

  lazy val plugin = Project(
    "xsbt-scalate-generator",
    file("plugin"),
    settings = buildSettings ++ Seq(
      sbtPlugin := true
    )
  )
}
