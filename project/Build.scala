import com.earldouglas.xwp.XwpPlugin
import sbt._
import Keys._
import wartremover._

object Build extends Build {

  XwpPlugin.jetty()

  val jettyVersion = "9.2.6.v20141205"

  val defaults = Seq(
    organization := "com.casualmiracles",
    scalaVersion := "2.11.5",
    scalacOptions in (Compile, compile) += "-deprecation"
  )

  val coreSettings = Seq(
    libraryDependencies ++= List(
      "io.reactivex"      %% "rxscala"        % "0.23.1"      % "compile",
      "net.liftweb"       %% "lift-webkit"    % "2.6"         % "compile"
    ),
    wartremoverErrors ++= Warts.all,
    wartremoverExcluded ++= Seq("com.casualmiracles.rxlift.RxCometActor", "code.comet.Tick")
  )

  val exampleSettings = Seq(
    libraryDependencies ++= List(
      "net.liftmodules"   %% "lift-jquery-module_2.6" % "2.8"         % "compile",
      "org.eclipse.jetty" % "jetty-webapp"            % jettyVersion  % "container, test, compile",
      "org.eclipse.jetty" % "jetty-plus"              % jettyVersion  % "container, test, compile",
      "org.eclipse.jetty" % "jetty-servlets"          % jettyVersion  % "container, test, compile"
    ),
    wartremoverErrors ++= Warts.allBut(Wart.Var, Wart.NonUnitStatements),
    wartremoverExcluded ++= Seq("com.casualmiracles.rxlift.RxCometActor", "code.comet.Tick")
  )

  lazy val core = Project(
    "core", file("core"),
    settings = defaults ++ coreSettings)

  lazy val example = Project(
    "example", file("example"),
    settings = defaults ++ exampleSettings ++ XwpPlugin.warSettings ++ XwpPlugin.webappSettings ++ XwpPlugin.jetty(port = 8080)
  ).dependsOn(core)

  lazy val root = Project("rxlift", file(".")).aggregate(core, example)
}
