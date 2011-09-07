import sbt._
import Keys._

object ScalateGenerateBuild extends Build {
    
  val buildSettings = Defaults.defaultSettings ++ Seq(
    version := "0.0.2-SNAPSHOT",
    organization := "com.mojolly.scalate",
    publishTo <<= (version) { version: String =>
      val nexus = "http://nexus.scala-tools.org/content/repositories/"
      if (version.trim.endsWith("SNAPSHOT")) Some("snapshots" at nexus+"snapshots/")
      else                                   Some("releases" at nexus+"releases/")
    }
  )


  lazy val root = Project("xsbt-scalate", file("."), settings = buildSettings) aggregate (generator, plugin)

  lazy val generator = Project(
    "scalate-generator",
    file("generator"),
    settings = buildSettings ++ Seq(
      scalaVersion := "2.9.1",
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
