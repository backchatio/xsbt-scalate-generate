import sbt._
import Keys._

object ScalateGenerateLibraryBuild extends Build {
  val root = Project("scalate-generator", file("."))
}