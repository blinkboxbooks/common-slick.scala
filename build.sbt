val buildSettings = Seq(
  name := "common-slick",
  organization := "com.blinkbox.books.platform",
  version := scala.util.Try(scala.io.Source.fromFile("VERSION").mkString.trim).getOrElse("0.0.0"),
  scalaVersion  := "2.10.4",
  scalacOptions := Seq("-unchecked", "-deprecation", "-feature", "-encoding", "utf8", "-target:jvm-1.7")
)

val dependencySettings = Seq(
  libraryDependencies ++= {
    Seq(
      "com.typesafe.slick"        %%  "slick"                 % "2.1.0",
      "com.blinkbox.books"        %%  "common-config"         % "1.0.0",
      "mysql"                     %   "mysql-connector-java"  % "5.1.31",
      "com.blinkbox.books"        %%  "common-scala-test"     % "0.2.0"   % "test",
      "com.h2database"            %   "h2"                    % "1.4.181",
      "joda-time"                 %   "joda-time"             % "2.4",
      "org.joda"                  %   "joda-convert"          % "1.7"
    )
  }
)

val root = (project in file(".")).
  settings(buildSettings: _*).
  settings(dependencySettings: _*)
