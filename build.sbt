name := "sapphire-data"

crossScalaVersions := Seq("2.13.6", "3.0.1", "2.12.13")

scalaVersion := crossScalaVersions.value.head

ThisBuild / versionScheme := Some("early-semver")

compileOrder := CompileOrder.JavaThenScala

lazy val sapphire_data_root = Project(id = "sapphire-data", base = file("."))

scalacOptions += "-deprecation"

test / parallelExecution := false

val LogbackVersion = "1.2.3"

val JavaFXVersion = "16"

val osName = System.getProperty("os.name") match {
  case n if n.startsWith("Linux")   => "linux"
  case n if n.startsWith("Mac")     => "mac"
  case n if n.startsWith("Windows") => "win"
  case _                            => throw new Exception("Unknown platform!")
}

// Test

libraryDependencies += "org.scalameta" %% "munit" % "0.7.26" % Test

libraryDependencies += "ch.qos.logback" % "logback-classic" % LogbackVersion % Test

val circeVersion = "0.14.1"

libraryDependencies ++= Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-parser"
).map(_ % circeVersion % Test)

// Compile

libraryDependencies ++= Seq("base").map(m => "org.openjfx" % s"javafx-$m" % JavaFXVersion % Provided classifier osName)

// Environment

libraryDependencies += "org.scala-lang.modules" %% "scala-collection-compat" % "2.4.4"

libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.9.4"

libraryDependencies += "com.typesafe" % "config" % "1.4.1"

// Expression Language

libraryDependencies += "org.apache.tomcat" % "tomcat-jasper-el" % "9.0.48"

// optional report support
resolvers += "jasperreports-repo" at "https://jaspersoft.jfrog.io/jaspersoft/third-party-ce-artifacts"

libraryDependencies += "net.sf.jasperreports" % "jasperreports" % "6.17.0" % Provided

libraryDependencies += ("com.github.pathikrit" %% "better-files" % "3.9.1" % Provided).cross(CrossVersion.for3Use2_13)

enablePlugins(BuildInfoPlugin)

buildInfoPackage := "com.sfxcode.sapphire.data"

buildInfoOptions += BuildInfoOption.BuildTime
