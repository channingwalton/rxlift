import sbt._
import Keys._
  
object Build extends Build {

  val defaults = Seq(
    organization := "com.casualmiracles",
    scalaVersion := "2.11.5",

    scalacOptions in (Compile, compile) += "-deprecation",
    libraryDependencies ++= List(
      "org.scalaz" %% "scalaz-core" % "7.1.0" % "compile",
      "io.reactivex" %% "rxscala" % "0.23.1" % "compile",
      "net.liftweb" %% "lift-webkit" % "2.6" % "compile"
    )
  )

  lazy val core = project.settings(defaults: _*)

  lazy val example = project.settings(defaults: _*).dependsOn(core)

  lazy val root = Project("rxlift", file(".")).aggregate(core, example)
}
