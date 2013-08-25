import sbt._
import Keys._
import scala.xml.Group

object ScalateGenerateBuild extends Build {

 
  val buildSettings = Defaults.defaultSettings ++ Seq(
    scalacOptions ++= Seq("-unchecked", "-deprecation"),
    javacOptions ++= Seq("-target", "1.6", "-source", "1.6"),
    scalaVersion := "2.10.0",
    crossScalaVersions := Seq("2.9.1", "2.9.0-1", "2.9.0", "2.9.1-1", "2.9.2", "2.9.3", "2.10.0"),
    organization := "com.mojolly.scalate",
    licenses := Seq(
      "MIT" -> new URL("https://github.com/backchatio/xsbt-scalate-generate/blob/master/LICENSE")
    ),
    pomExtra <<= (pomExtra, name, description) {(pom, name, desc) => pom ++ Group(
      <url>http://github.com/backchatio/xsbt-scalate-generate</url>
      <scm>
        <connection>scm:git:git://github.com/backchatio/xsbt-scalate-generate.git</connection>
        <developerConnection>scm:git:git@github.com:mojolly/xsbt-scalate-generate.git</developerConnection>
        <url>https://github.com/backchatio/xsbt-scalate-generate.git</url>
      </scm>
      <developers>
        <developer>
          <id>casualjim</id>
          <name>Ivan Porto Carrero</name>
          <url>http://flanders.co.nz/</url>
        </developer>
        <developer>
          <id>sdb</id>
          <name>Stefan De Boey</name>
          <url>http://stefandeboey.be/</url>
        </developer>
        <developer>
          <id>BowlingX</id>
          <name>David Heidrich</name>
          <url>http://www.myself-design.com/</url>
        </developer>
      </developers>
    )}
  )



  lazy val generator = file("generator")

  lazy val plugin = file("plugin")
  
  lazy val root = Project(
                          "xsbt-scalate", 
                          file("."), 
                          settings = buildSettings ++ Seq(publish := {}, publishLocal := {})) dependsOn(generator, plugin) aggregate (generator, plugin)
}
