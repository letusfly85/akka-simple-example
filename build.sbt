organization := "io.wonder.soft"

name := "akka-simple-example"

version := "0.1"

scalaVersion := "2.12.4"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

dependencyOverrides += "org.scala-lang" %% "scala-compiler" % scalaVersion.value

libraryDependencies ++= {
  val akkaV       = "2.5.8"
  val akkaStreamV       = "10.1.0"
  Seq(
    "com.typesafe.akka" %% "akka-actor"              % akkaV,
    "com.typesafe.akka" %% "akka-persistence"        % akkaV,
    "com.typesafe.akka" %% "akka-slf4j"              % akkaV,
    "ch.qos.logback"    %  "logback-classic"         % "1.1.7",
    "com.typesafe.akka" %% "akka-stream"             % akkaV,

    "com.typesafe.akka" %% "akka-http-core"          % akkaStreamV,
    "com.typesafe.akka" %% "akka-http"               % akkaStreamV,
    "com.typesafe.akka" %% "akka-http-spray-json"    % akkaStreamV,
    "com.typesafe.akka" %% "akka-http-testkit"       % akkaStreamV,

    "commons-io" % "commons-io" % "2.5",

    "org.specs2" % "specs2_2.12" % "2.4.17" % Test,

    "commons-configuration" % "commons-configuration" % "1.10"
  )
}

//refs: https://github.com/gerferra/amphip/blob/master/build.sbt
scalacOptions ++= Seq(
  "-Ypatmat-exhaust-depth", "off"
)
