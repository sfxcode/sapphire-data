import sbt.url
// publish

organization := "com.sfxcode.sapphire"

ThisBuild / organization := "com.sfxcode.sapphire"
ThisBuild / organizationHomepage := Some(url("https://github.com/sfxcode"))

releaseCrossBuild := true

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

licenses += ("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0.html"))

// add sonatype repository settings
// snapshot versions publish to sonatype snapshot repository
// other versions publish to sonatype staging repository
publishTo := sonatypePublishToBundle.value

import ReleaseTransformations._

releaseCrossBuild := true // true if you cross-build the project for multiple Scala versions
releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  runTest,
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  // For non cross-build projects, use releaseStepCommand("publishSigned")
  releaseStepCommandAndRemaining("+publishSigned"),
  releaseStepCommand("sonatypeBundleRelease"),
  setNextVersion,
  commitNextVersion,
  pushChanges
)
