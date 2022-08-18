name := "sapphire-data"

crossScalaVersions := Seq("2.13.8", "3.1.3", "2.12.13")

scalaVersion := crossScalaVersions.value.head

ThisBuild / versionScheme := Some("early-semver")

compileOrder := CompileOrder.JavaThenScala

lazy val sapphire_data_root = Project(id = "sapphire-data", base = file("."))

scalacOptions += "-deprecation"

test / parallelExecution := false

// Test

libraryDependencies += "org.scalameta" %% "munit" % "0.7.29" % Test

libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.11" % Test

val circeVersion = "0.14.2"

libraryDependencies ++= Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-parser"
).map(_ % circeVersion % Test)

// Environment

libraryDependencies += "org.scala-lang.modules" %% "scala-collection-compat" % "2.8.1"

libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.9.5"

libraryDependencies += "com.typesafe" % "config" % "1.4.2"

// Expression Language

libraryDependencies += "org.apache.tomcat" % "tomcat-jasper-el" % "10.0.23"

// optional report support

resolvers += "jasperreports-repo".at("https://jaspersoft.jfrog.io/jaspersoft/third-party-ce-artifacts")

libraryDependencies += "net.sf.jasperreports" % "jasperreports" % "6.20.0" % Provided

libraryDependencies += ("com.github.pathikrit" %% "better-files" % "3.9.1" % Provided).cross(CrossVersion.for3Use2_13)

enablePlugins(BuildInfoPlugin)

buildInfoPackage := "com.sfxcode.sapphire.data"

buildInfoOptions += BuildInfoOption.BuildTime
