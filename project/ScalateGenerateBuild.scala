import sbt._
import Keys._

object ScalateGenerateBuild extends Build {

  val buildVersion = "0.1.6-SNAPSHOT"
    
  val buildSettings = Defaults.defaultSettings ++ Seq(
    version := buildVersion,
    scalaVersion := "2.9.1",
    crossScalaVersions := Seq("2.9.1", "2.9.0-1", "2.9.0", "2.9.1-1"),
    organization := "com.mojolly.scalate",
    externalResolvers <<= resolvers map { rs => Resolver.withDefaultResolvers(rs, mavenCentral = true, scalaTools = false) },
    publishTo <<= version { (v: String) =>
      val nexus = "https://oss.sonatype.org/"
      if (v.trim.endsWith("SNAPSHOT"))
        Some("snapshots" at nexus + "content/repositories/snapshots")
      else
        Some("releases"  at nexus + "service/local/staging/deploy/maven2")
    },
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
