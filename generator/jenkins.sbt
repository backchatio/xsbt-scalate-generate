import xml.Group

name := "scalate-generator"

scalacOptions ++= Seq("-unchecked", "-deprecation")

javacOptions ++= Seq("-target", "1.6", "-source", "1.6")

scalaVersion := "2.9.2"

crossScalaVersions := Seq("2.9.1", "2.9.0-1", "2.9.0", "2.9.1-1", "2.9.2", "2.10.0")

organization := "com.mojolly.scalate"

licenses := Seq(
  "MIT" -> new URL("https://github.com/backchatio/xsbt-scalate-generate/blob/master/LICENSE")
)

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

libraryDependencies <+= (scalaVersion) {
  case v if v.startsWith("2.9") => "org.fusesource.scalate" % "scalate-core_2.9" % "1.6.0" % "compile"
  case _ => "org.fusesource.scalate" % "scalate-core_2.10" % "1.6.0" % "compile"
}

publishTo <<= version { (v: String) =>
  val nexus = "https://oss.sonatype.org/"
  if (v.trim.endsWith("SNAPSHOT"))
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}

publishMavenStyle := true

