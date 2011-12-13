import sbt._
import Keys._

object ScalateGenerateBuild extends Build {

  val buildVersion = "0.1.4"
    
  val buildSettings = Defaults.defaultSettings ++ Seq(
    version := buildVersion,
    organization := "com.mojolly.scalate",
    publishTo <<= (version) { version: String =>
      val nexus = "http://nexus.scala-tools.org/content/repositories/"
      if (version.trim.endsWith("SNAPSHOT")) Some("snapshots" at nexus+"snapshots/")
      else                                   Some("releases" at nexus+"releases/")
    },
    credentials += Credentials(Path.userHome / ".ivy2" / ".scala_tools_credentials"),
    publishMavenStyle := true,
    licenses := Seq(
      "MIT" -> new URL("https://github.com/mojolly/xsbt-scalate-generate/blob/master/LICENSE")
    ),
    projectID <<= (organization,moduleName,version,artifacts,crossPaths){ (org,module,version,as,crossEnabled) =>
      ModuleID(org, module, version).cross(crossEnabled).artifacts(as : _*)
    }
  )


  val versionGen     = TaskKey[Seq[File]]("version-gen")
  lazy val root = Project("xsbt-scalate", file("."), settings = buildSettings) aggregate (generator, plugin)

  lazy val generator = Project(
    "scalate-generator",
    file("generator"),
    settings = buildSettings ++ Seq(
      scalaVersion := "2.9.1",
      libraryDependencies += "org.fusesource.scalate" % "scalate-core" % "1.5.3" % "compile"
    )
  )

  lazy val plugin = Project(
    "xsbt-scalate-generator",
    file("plugin"),
    settings = buildSettings ++ Seq(
      sbtPlugin := true,
      versionGen     <<= (sourceManaged in Compile, name, organization) map {
          (sourceManaged:File, name:String, vgp:String) =>
              val file  = sourceManaged / vgp.replace(".","/") / "Version.scala"
              val code  = 
                      (
                          if (vgp != null && vgp.nonEmpty)  "package " + vgp + "\n"
                          else              ""
                      ) +
                      "object Version {\n" + 
                      "  val name\t= \"" + name + "\"\n" + 
                      "  val version\t= \"" + buildVersion + "\"\n" + 
                      "}\n"  
              IO write (file, code)
              Seq(file)
      },
      sourceGenerators in Compile <+= versionGen,
      version <<= (sbtVersion, version)(_ + "-" + _)
    )
  )
}
