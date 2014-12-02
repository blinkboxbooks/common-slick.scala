lazy val root = (project in file(".")).
  settings(
    name := "common-slick",
    organization := "com.blinkbox.books",
    version := scala.util.Try(scala.io.Source.fromFile("VERSION").mkString.trim).getOrElse("0.0.0"),
    scalaVersion := "2.11.4",
    crossScalaVersions := Seq("2.11.4"),
    scalacOptions := Seq("-unchecked", "-deprecation", "-feature", "-encoding", "utf8", "-target:jvm-1.7", "-Xfatal-warnings", "-Xfuture"),
    libraryDependencies ++= {
      val metricsV = "3.1.0"
      Seq(
        "com.typesafe.slick"        %%  "slick"                 % "2.1.0",
        "mysql"                     %   "mysql-connector-java"  % "5.1.31",
        "com.blinkbox.books"        %%  "common-scala-test"     % "0.3.0"   % Test,
        "com.h2database"            %   "h2"                    % "1.4.181",
        "joda-time"                 %   "joda-time"             % "2.4",
        "org.joda"                  %   "joda-convert"          % "1.7",
        "io.dropwizard.metrics"     %   "metrics-core"          % metricsV,
        "io.dropwizard.metrics"     %   "metrics-healthchecks"  % metricsV
      )
    }
  )
