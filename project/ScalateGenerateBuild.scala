import sbt._
import Keys._

object ScalateGenerateBuild extends Build {
    
  val buildSettings = Defaults.defaultSettings ++ Seq(
    version := "0.1.1-SNAPSHOT",
    organization := "com.mojolly.scalate",
    publishTo <<= (version) { version: String =>
      val nexus = "http://nexus.scala-tools.org/content/repositories/"
      if (version.trim.endsWith("SNAPSHOT")) Some("snapshots" at nexus+"snapshots/")
      else                                   Some("releases" at nexus+"releases/")
    },
    credentials += Credentials(Path.userHome / ".ivy2" / ".scala_tools_credentials"),
    publishMavenStyle := true
  )


  val versionGen     = TaskKey[Seq[File]]("version-gen")
  lazy val root = Project("xsbt-scalate", file("."), settings = buildSettings) aggregate (generator, plugin)

  lazy val generator = Project(
    "scalate-generator",
    file("generator"),
    settings = buildSettings ++ Seq(
      scalaVersion := "2.9.1",
      libraryDependencies += "org.fusesource.scalate" % "scalate-core" % "1.5.3" % "compile",
      exportJars := true
    )
  )

  lazy val plugin = Project(
    "xsbt-scalate-generator",
    file("plugin"),
    settings = buildSettings ++ Seq(
      sbtPlugin := true,
      versionGen     <<= (sourceManaged in Compile, name, version, organization) map {
          (sourceManaged:File, name:String, version:String, vgp:String) =>
              val file  = sourceManaged / vgp.replace(".","/") / "Version.scala"
              val code  = 
                      (
                          if (vgp != null && vgp.nonEmpty)  "package " + vgp + "\n"
                          else              ""
                      ) +
                      "object Version {\n" + 
                      "  val name\t= \"" + name + "\"\n" + 
                      "  val version\t= \"" + version + "\"\n" + 
                      "}\n"  
              IO write (file, code)
              Seq(file)
      },
      sourceGenerators in Compile <+= versionGen map identity
    )
  )
}
