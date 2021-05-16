name := "sapphire-data"

crossScalaVersions := Seq("2.13.5", "2.12.13")

scalaVersion := crossScalaVersions.value.head

ThisBuild / versionScheme := Some("early-semver")

compileOrder := CompileOrder.JavaThenScala

lazy val sapphire_data_root = Project(id = "sapphire-data", base = file("."))

scalacOptions += "-deprecation"

test / parallelExecution := false

val Json4sVersion  = "3.6.11"
val LogbackVersion = "1.2.3"

lazy val docs = (project in file("docs"))
  .enablePlugins(ParadoxSitePlugin)
  .enablePlugins(ParadoxMaterialThemePlugin)
  .enablePlugins(GhpagesPlugin)
  .settings(
    scalaVersion := "2.13.5",
    name := "sapphire-data-docs",
    publish / skip := true,
    ghpagesNoJekyll := true,
    previewFixedPort := Some(9016),
    git.remoteRepo := "git@github.com:sfxcode/sapphire-data.git",
    Compile / paradoxMaterialTheme ~= {
      _.withRepository(uri("https://github.com/sfxcode/sapphire-data"))

    },
    (Compile / paradoxMarkdownToHtml / excludeFilter) := (Compile / paradoxMarkdownToHtml / excludeFilter).value ||
    ParadoxPlugin.InDirectoryFilter((Compile / paradox / sourceDirectory).value / "includes")
  )

val JavaFXVersion = "16"

val osName = System.getProperty("os.name") match {
  case n if n.startsWith("Linux")   => "linux"
  case n if n.startsWith("Mac")     => "mac"
  case n if n.startsWith("Windows") => "win"
  case _                            => throw new Exception("Unknown platform!")
}

// Test

libraryDependencies += "org.specs2" %% "specs2-core" % "4.11.0" % Test

libraryDependencies += "org.json4s" %% "json4s-native" % Json4sVersion % Test

libraryDependencies += "ch.qos.logback" % "logback-classic" % LogbackVersion % Test

// Compile

libraryDependencies ++= Seq("base").map(m => "org.openjfx" % s"javafx-$m" % JavaFXVersion % Provided classifier osName)

// Environment

libraryDependencies += "org.scala-lang.modules" %% "scala-collection-compat" % "2.4.3"

libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.9.3"

libraryDependencies += "com.typesafe" % "config" % "1.4.1"

// Expression Language

libraryDependencies += "org.apache.tomcat" % "tomcat-jasper-el" % "9.0.45"

// optional report support
resolvers += "jasperreports-repo" at "https://jaspersoft.jfrog.io/jaspersoft/third-party-ce-artifacts"

libraryDependencies += "net.sf.jasperreports" % "jasperreports" % "6.17.0" % Provided

libraryDependencies += "com.github.pathikrit" %% "better-files" % "3.9.1" % Provided

enablePlugins(BuildInfoPlugin)

buildInfoPackage := "com.sfxcode.sapphire.data"

buildInfoOptions += BuildInfoOption.BuildTime

packageOptions += {
  Package.ManifestAttributes(
    "Created-By"               -> "Simple Build Tool",
    "Built-By"                 -> "sfxcode",
    "Build-Jdk"                -> System.getProperty("java.version"),
    "Specification-Title"      -> name.value,
    "Specification-Version"    -> version.value,
    "Specification-Vendor"     -> organization.value,
    "Implementation-Title"     -> name.value,
    "Implementation-Version"   -> version.value,
    "Implementation-Vendor-Id" -> organization.value,
    "Implementation-Vendor"    -> organization.value
  )
}
