ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.8"

lazy val root = (project in file("."))
  .settings(
    name := "rss-news-extraction"
  )

libraryDependencies ++= Seq(
  "io.spray" %%  "spray-json" % "1.3.6",
  "ch.qos.logback" % "logback-classic" % "1.2.11",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.5",
  "org.scalatest" %% "scalatest" % "3.2.12" % Test,
  "org.scalamock" %% "scalamock" % "5.2.0" % Test,
  "de.l3s.boilerpipe" % "boilerpipe" % "1.1.0",
  "com.h2database" % "h2" % "1.4.196"
)