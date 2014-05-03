import scala.xml.Group

lazy val generator = project

lazy val plugin = project

scalacOptions ++= Seq("-unchecked", "-deprecation")

javacOptions ++= Seq("-target", "1.6", "-source", "1.6")

scalaVersion := "2.11.0"

crossScalaVersions := Seq("2.9.1", "2.9.0-1", "2.9.0", "2.9.1-1", "2.9.2", "2.9.3", "2.10.0", "2.11.0")

organization in ThisBuild := "com.mojolly.scalate"

licenses in ThisBuild := Seq(
  "MIT" -> new URL("https://github.com/backchatio/xsbt-scalate-generate/blob/master/LICENSE")
)

publish := {}

publishLocal := {}

// publishSigned := {}

pomExtra in ThisBuild <<= (pomExtra, name, description) {(pom, name, desc) => pom ++ Group(
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
  </developers>) }

