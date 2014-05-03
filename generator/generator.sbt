import xml.Group

name := "scalate-generator"

scalacOptions ++= Seq("-unchecked", "-deprecation")

javacOptions ++= Seq("-target", "1.6", "-source", "1.6")

scalaVersion := "2.10.0"

crossScalaVersions := Seq("2.9.1", "2.9.0-1", "2.9.0", "2.9.1-1", "2.9.2", "2.9.3", "2.10.0", "2.11.0")

libraryDependencies <+= (scalaVersion) {
  case v if v.startsWith("2.9") => "org.fusesource.scalate" % "scalate-core_2.9" % "1.6.1" % "compile"
  case v if v.startsWith("2.10") => "org.fusesource.scalate" % "scalate-core_2.10" % "1.6.1" % "compile"
  case _ => "org.scalatra.scalate" %% "scalate-core" % "1.7.0" % "compile"
}

publishTo <<= version { (v: String) =>
  val nexus = "https://oss.sonatype.org/"
  if (v.trim.endsWith("SNAPSHOT"))
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}

publishMavenStyle := true

