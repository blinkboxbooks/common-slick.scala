val buildSettings = Seq(
  name := "common-slick",
  organization := "com.blinkbox.books",
  version := scala.util.Try(scala.io.Source.fromFile("VERSION").mkString.trim).getOrElse("0.0.0"),
  crossScalaVersions := Seq("2.10.4", "2.11.2"),
  scalacOptions := Seq("-unchecked", "-deprecation", "-feature", "-encoding", "utf8", "-target:jvm-1.7")
)

val dependencySettings = Seq(
  libraryDependencies ++= {
    val metricsV = "3.0.2"
    Seq(
      "com.typesafe.slick"        %%  "slick"                 % "2.1.0",
      "mysql"                     %   "mysql-connector-java"  % "5.1.31",
      "com.blinkbox.books"        %%  "common-scala-test"     % "0.3.0"   % Test,
      "com.h2database"            %   "h2"                    % "1.4.181",
      "joda-time"                 %   "joda-time"             % "2.4",
      "org.joda"                  %   "joda-convert"          % "1.7",
      "com.codahale.metrics"      %   "metrics-core"          % metricsV,
      "com.codahale.metrics"      %   "metrics-healthchecks"  % metricsV
    )
  }
)

val root = (project in file(".")).
  settings(buildSettings: _*).
  settings(dependencySettings: _*)
