import sbt.url

import scala.xml.transform.{RewriteRule, RuleTransformer}
import scala.xml.{Comment, Elem, Node => XmlNode, NodeSeq => XmlNodeSeq}

name := "sapphire-data"

organization := "com.sfxcode.sapphire"

crossScalaVersions := Seq("2.13.4", "2.12.12")

scalaVersion := crossScalaVersions.value.head

compileOrder := CompileOrder.JavaThenScala

lazy val sapphire_data_root = Project(id = "sapphire-data", base = file("."))

javacOptions in test += "-Dorg.apache.deltaspike.ProjectStage=Test"

scalacOptions += "-deprecation"

parallelExecution in Test := false

val Json4sVersion     = "3.6.10"
val LogbackVersion    = "1.2.3"
val DeltaspikeVersion = "1.9.4"

lazy val docs = (project in file("docs"))
  .enablePlugins(ParadoxSitePlugin)
  .enablePlugins(ParadoxMaterialThemePlugin)
  .enablePlugins(GhpagesPlugin)
  .settings(
    scalaVersion := "2.13.4",
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

val JavaFXVersion = "16-ea+6"

val osName = System.getProperty("os.name") match {
  case n if n.startsWith("Linux")   => "linux"
  case n if n.startsWith("Mac")     => "mac"
  case n if n.startsWith("Windows") => "win"
  case _                            => throw new Exception("Unknown platform!")
}

// Test

libraryDependencies += "org.specs2" %% "specs2-core" % "4.10.5" % Test

libraryDependencies += "org.json4s" %% "json4s-native" % Json4sVersion % Test

libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.3" % Test

// Compile

libraryDependencies ++= Seq("base").map(m => "org.openjfx" % s"javafx-$m" % JavaFXVersion % Provided classifier osName)

// Environment

libraryDependencies += "org.scala-lang.modules" %% "scala-collection-compat" % "2.4.1"

libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2"

libraryDependencies += "com.typesafe" % "config" % "1.4.1"

// Expression Language

libraryDependencies += "org.apache.tomcat" % "tomcat-jasper-el" % "10.0.0"

// optional report support
resolvers += "jasperreports-repo" at "https://jaspersoft.jfrog.io/jaspersoft/third-party-ce-artifacts"

libraryDependencies += "net.sf.jasperreports" % "jasperreports" % "6.16.0" % Provided

libraryDependencies += "com.github.pathikrit" %% "better-files" % "3.9.1" % Provided

licenses += ("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0.html"))

enablePlugins(BuildInfoPlugin)

buildInfoPackage := "com.sfxcode.sapphire.data"

buildInfoOptions += BuildInfoOption.BuildTime

// publish

// Use `pomPostProcess` to remove dependencies marked as "provided" from publishing in POM
// This is to avoid dependency on wrong OS version JavaFX libraries [Issue #289]
// See also [https://stackoverflow.com/questions/27835740/sbt-exclude-certain-dependency-only-during-publish]

pomPostProcess := { node: XmlNode =>
  new RuleTransformer(new RewriteRule {
    override def transform(node: XmlNode): XmlNodeSeq =
      node match {
        case e: Elem
            if e.label == "dependency" && e.child.exists(c => c.label == "scope" && c.text == "provided")
              && e.child.exists(c => c.label == "groupId" && c.text == "org.openjfx") =>
          val organization = e.child.filter(_.label == "groupId").flatMap(_.text).mkString
          val artifact     = e.child.filter(_.label == "artifactId").flatMap(_.text).mkString
          val version      = e.child.filter(_.label == "version").flatMap(_.text).mkString
          Comment(s"provided dependency $organization#$artifact;$version has been omitted")
        case _ => node
      }
  }).transform(node).head
}

releaseCrossBuild := true

bintrayReleaseOnPublish in ThisBuild := true

publishMavenStyle := true

homepage := Some(url("https://github.com/sfxcode/sapphire-data"))

scmInfo := Some(
  ScmInfo(
    url("https://github.com/sfxcode/sapphire-data"),
    "scm:https://github.com/sfxcode/sapphire-data.git"
  )
)

developers := List(
  Developer(
    id = "sfxcode",
    name = "Tom Lamers",
    email = "tom@sfxcode.com",
    url = url("https://github.com/sfxcode")
  )
)

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
