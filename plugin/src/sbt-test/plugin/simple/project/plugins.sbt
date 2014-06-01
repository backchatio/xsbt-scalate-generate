resolvers += Resolver.file("ivy-local", file(Path.userHome.absolutePath + "/.ivy2/local"))(Resolver.mavenStylePatterns)

{
  val pluginVersion = System.getProperty("plugin.version")
  if (pluginVersion == null) {
    throw new RuntimeException("""|The system property 'plugin.version' is not defined.
                                  |Specify this property using the scriptedLaunchOpts -D.""".stripMargin)
  } else {
    addSbtPlugin("com.mojolly.scalate" % "xsbt-scalate-generator" % pluginVersion)
  }
}
