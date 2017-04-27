
val jettyVersion = "9.4.4.v20170414"

val defaults = Seq(
  organization := "com.casualmiracles",
  scalaVersion := "2.12.2",
  scalacOptions in (Compile, compile) += "-deprecation"
)

val coreSettings = Seq(
  libraryDependencies ++= List(
    "io.reactivex"      %% "rxscala"        % "0.26.5"   % "compile",
    "org.scalaz"        %% "scalaz-core"    % "7.2.12"   % "compile",
    "net.liftweb"       %% "lift-webkit"    % "3.0.1"    % "compile"
  )
)

val exampleSettings = Seq(
  libraryDependencies ++= List(
    "net.liftmodules"   %% "lift-jquery-module_3.0" % "2.10"        % "compile",
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
