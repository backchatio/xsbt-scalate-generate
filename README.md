# Scalate support for SBT 0.10.x
 
Integration for SBT that lets you generate sources for your Scalate templates and precompile them as part of the normal compilation process. This plugin is published to scala-tools.org.
 
## Usage

Include the plugin in `project/plugins.sbt`:

```sbt
resolvers += ScalaToolsSnapshots

libraryDependencies <+= sbtVersion(v => "com.mojolly.scalate" %% "xsbt-scalate-generator" % (v + "-0.1.4"))
```

Configure the plugin in `build.sbt`:

```sbt
seq(scalateSettings:_*)
      
scalateTemplateDirectory in Compile <<= (baseDirectory) {
  (basedir) => new File(basedir, "src/main/webapp/WEB-INF")
}
    
scalateImports ++= Seq(
  "import foo._",
  "import bar._"
)
    
scalateBindings += Binding("flash", "org.scalatra.FlashMap")
```

## Patches

Patches are gladly accepted from their original author. Along with any patches, please state that the patch is your original work and that you license the work to the *xsbt-scalate-generate* project under the MIT License.
 
## License
 
MIT licensed. Check the [LICENSE](https://raw.github.com/mojolly/xsbt-scalate-generate/master/LICENSE) file.
