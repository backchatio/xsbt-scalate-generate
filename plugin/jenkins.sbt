import xml.Group

name := "xsbt-scalate-generator"

scalacOptions ++= Seq("-unchecked", "-deprecation")

javacOptions ++= Seq("-target", "1.6", "-source", "1.6")

scalaVersion := "2.9.1"

organization := "com.mojolly.scalate"

externalResolvers <<= resolvers map { rs => Resolver.withDefaultResolvers(rs, mavenCentral = true, scalaTools = false) }

licenses := Seq(
  "MIT" -> new URL("https://github.com/mojolly/xsbt-scalate-generate/blob/master/LICENSE")
)

projectID <<= (organization,moduleName,version,artifacts,crossPaths){ (org,module,version,as,crossEnabled) =>
  ModuleID(org, module, version).cross(crossEnabled).artifacts(as : _*)
}

pomExtra <<= (pomExtra, name, description) {(pom, name, desc) => pom ++ Group(
  <url>http://github.com/mojolly/xsbt-scalate-generate</url>
  <scm>
    <connection>scm:git:git://github.com/mojolly/xsbt-scalate-generate.git</connection>
    <developerConnection>scm:git:git@github.com:mojolly/xsbt-scalate-generate.git</developerConnection>
    <url>https://github.com/mojolly/xsbt-scalate-generate.git</url>
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
  </developers>
)}

sbtPlugin := true

sourceGenerators in Compile <+= (sourceManaged in Compile, name, organization, version) map {
    (sourceManaged:File, name:String, vgp:String, buildVersion) =>
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
}

publishMavenStyle := false

publishTo <<= (version) { version: String =>
   val scalasbt = "http://scalasbt.artifactoryonline.com/scalasbt/"
   val (name, url) = if (version.contains("-SNAPSHOT"))
                       ("sbt-plugin-snapshots", scalasbt+"sbt-plugin-snapshots")
                     else
                       ("sbt-plugin-releases", scalasbt+"sbt-plugin-releases")
   Some(Resolver.url(name, new URL(url))(Resolver.ivyStylePatterns))
}