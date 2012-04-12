# Scalate support for SBT 0.1x.x
 
Integration for SBT that lets you generate sources for your Scalate templates and precompile them as part of the normal compilation process. This plugin is published to scala-tools.org.
 
## Usage

Include the plugin in `project/plugins.sbt`:

```scala
libraryDependencies <+= sbtVersion(v => "com.mojolly.scalate" %% "xsbt-scalate-generator" % (v + "-0.1.6"))
```

or as a git dependency in `project/project/build.scala`:

```scala
import sbt._
import Keys._

object PluginsBuild extends Build {
  lazy val root = Project("plugins", file(".")) dependsOn (scalateGenerate) settings (scalacOptions += "-deprecation")
  lazy val scalateGenerate = ProjectRef(uri("git://github.com/mojolly/xsbt-scalate-generate.git"), "xsbt-scalate-generator")
}
```

Configure the plugin in `build.sbt`:

```scala
// import must be at top of build.sbt, or SBT will complain
import com.mojolly.scalate.ScalatePlugin._

seq(scalateSettings:_*)
      
scalateTemplateDirectory in Compile <<= (baseDirectory) { _ / "src/main/webapp/WEB-INF" }

scalateImports ++= Seq(
  "import scalaz._",
  "import Scalaz._",
  "import io.backchat.oauth2._",
  "import OAuth2Imports._",
  "import model._"
)

scalateBindings ++= Seq(
  Binding("flash", "scala.collection.Map[String, Any]", defaultValue = Some("Map.empty")),
  Binding("session", "org.scalatra.Session"),
  Binding("sessionOption", "Option[org.scalatra.Session]"),
  Binding("params", "scala.collection.Map[String, String]"),
  Binding("multiParams", "org.scalatra.MultiParams"),
  Binding("userOption", "Option[ResourceOwner]", defaultValue = Some("None")),
  Binding("user", "ResourceOwner", defaultValue = Some("null")),
  Binding("isAnonymous", "Boolean", defaultValue = Some("true")),
  Binding("isAuthenticated", "Boolean", defaultValue = Some("false")))
```

## Patches

Patches are gladly accepted from their original author. Along with any patches, please state that the patch is your original work and that you license the work to the *xsbt-scalate-generate* project under the MIT License.
 
## License
 
MIT licensed. Check the [LICENSE](https://raw.github.com/mojolly/xsbt-scalate-generate/master/LICENSE) file.
