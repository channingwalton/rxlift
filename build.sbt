enablePlugins(JettyPlugin)

val jettyVersion = "9.2.6.v20141205"

val defaults = Seq(
  organization := "com.casualmiracles",
  scalaVersion := "2.12.1",
  scalacOptions in (Compile, compile) += "-deprecation"
)

val coreSettings = Seq(
  libraryDependencies ++= List(
    "io.reactivex"      %% "rxscala"        % "0.26.4"      % "compile",
    "org.scalaz"        %% "scalaz-core"    % "7.3.0-M6"    % "compile",
    "net.liftweb"       %% "lift-webkit"    % "3.0"         % "compile"
  )
)

val exampleSettings = Seq(
  libraryDependencies ++= List(
    "net.liftmodules"   %% "lift-jquery-module_2.6" % "2.1"         % "compile",
    "org.eclipse.jetty" % "jetty-webapp"            % jettyVersion  % "container, test, compile",
    "org.eclipse.jetty" % "jetty-plus"              % jettyVersion  % "container, test, compile",
    "org.eclipse.jetty" % "jetty-servlets"          % jettyVersion  % "container, test, compile"
  )
)

lazy val core = Project(
  "core", file("core"),
  settings = defaults ++ coreSettings)

lazy val example = Project(
  "example", file("example"),
  settings = defaults ++ exampleSettings
).dependsOn(core)

lazy val root = Project("rxlift", file(".")).aggregate(core, example)
