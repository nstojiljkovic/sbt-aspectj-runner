/*
 * =========================================================================================
 * Copyright © 2013-2018 the kamon project <http://kamon.io/>
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 * =========================================================================================
 */

import ReleaseTransformations._
import sbt._
import sbt.Keys._
import sbt.Resolver.mavenLocal
import sbtrelease.ReleasePlugin

def crossSbtDependency(module: ModuleID, sbtVersion: String, scalaVersion: String): ModuleID = {
  Defaults.sbtPluginExtra(module, sbtVersion, scalaVersion)
}

val scala212Version = "2.12.8"
// val scala213Version = "2.13.0"

val aspectjTools = "org.aspectj" % "aspectjtools" % "1.9.4"
val playSbtPluginFor26 = "com.typesafe.play" % "sbt-plugin" % "2.6.23"
val playSbtPluginFor27 = "com.typesafe.play" % "sbt-plugin" % "2.7.3"

lazy val commonSettings = ReleasePlugin.extraReleaseCommands ++ Seq(
  organization := "com.github.nstojiljkovic",
  scalaVersion := scala212Version,
  crossScalaVersions := Seq(scala212Version),
  scalacOptions ++= Seq(
    "-deprecation",
    "-encoding", "UTF-8",
    "-feature",
    "-unchecked",
    "-Yno-adapted-args",
    "-Ywarn-dead-code",
    "-Ywarn-numeric-widen",
    "-Ywarn-value-discard",
    "-Xfuture"
  ),
  scalacOptions ++= {
    CrossVersion.partialVersion(scalaVersion.value) match {
      case Some((2, 12)) => Seq("-Xlint:-unused,_", "-Ywarn-unused:imports")
      case _ => Seq()
    }
  },
  resolvers ++= Seq(
    mavenLocal,
    "Restlet Repository" at "http://maven.restlet.org/",
    "JBoss Repository" at "https://repository.jboss.org/nexus/content/repositories/",
    "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/",
    "Scala-Tools Snapshots" at "http://scala-tools.org/repo-snapshots/"
  ),
  concurrentRestrictions in Global += Tags.limit(Tags.Test, 1),
  releasePublishArtifactsAction := PgpKeys.publishSigned.value,
  publishMavenStyle := true,
  publishTo := {
    val nexus = "https://oss.sonatype.org/"
    if (isSnapshot.value)
      Some("snapshots" at nexus + "content/repositories/snapshots")
    else
      Some("releases"  at nexus + "service/local/staging/deploy/maven2")
  },

  releaseCrossBuild := true,

  releaseProcess := Seq[ReleaseStep](
    checkSnapshotDependencies,
    inquireVersions,
    runClean,
    setReleaseVersion,
    commitReleaseVersion,
    tagRelease,
    releaseStepCommand("publishSigned"),
    setNextVersion,
    commitNextVersion,
    releaseStepCommand("sonatypeReleaseAll"),
    pushChanges
  ),
  pomExtra := (
    <url>https://github.com/nstojiljkovic/sbt-aspectj-runner</url>
      <licenses>
        <license>
          <name>Apache License 2.0</name>
          <url>https://raw.githubusercontent.com/nstojiljkovic/sbt-aspectj-runner/master/LICENSE</url>
          <distribution>repo</distribution>
        </license>
      </licenses>
      <scm>
        <url>git@github.com:nstojiljkovic/sbt-aspectj-runner.git</url>
        <connection>scm:git@github.com:nstojiljkovic/sbt-aspectj-runner.git</connection>
      </scm>
      <developers>
        <developer>
          <id>nstojiljkovic</id>
          <name>Nikola Stojiljković</name>
          <url>https://github.com/nstojiljkovic</url>
        </developer>
      </developers>)
)

lazy val root = (project in file("."))
  .settings(commonSettings: _*)
  .settings(
    name := "sbt-aspectj-runner",
    publishArtifact := false,
    crossSbtVersions := Seq("1.2.8")
  )
  .aggregate(aspectjRunner, aspectjRunnerPlay26, aspectjRunnerPlay27)

lazy val aspectjRunner = Project("sbt-aspectj-runner", file("sbt-aspectj-runner"))
  .settings(commonSettings: _*)
  .settings(
    sbtPlugin := true,
    crossSbtVersions := Seq("1.2.8"),
    libraryDependencies ++= Seq(aspectjTools)
  )

lazy val aspectjRunnerPlay26 = Project("sbt-aspectj-runner-play-26", file("sbt-aspectj-runner-play-2.6"))
  .settings(commonSettings: _*)
  .settings(
    sbtPlugin := true,
    crossSbtVersions := Seq("1.2.8"),
    moduleName := "sbt-aspectj-runner-play-2.6",
    libraryDependencies ++= Seq(
      aspectjTools,
      crossSbtDependency(playSbtPluginFor26, (sbtBinaryVersion in pluginCrossBuild).value, scalaBinaryVersion.value)
    )
  )
  .dependsOn(aspectjRunner)

lazy val aspectjRunnerPlay27 = Project("sbt-aspectj-runner-play-27", file("sbt-aspectj-runner-play-2.7"))
  .settings(commonSettings: _*)
  .settings(
    sbtPlugin := true,
    crossSbtVersions := Seq("1.2.8"),
    moduleName := "sbt-aspectj-runner-play-2.7",
    libraryDependencies ++= Seq(
      aspectjTools,
      crossSbtDependency(playSbtPluginFor27, (sbtBinaryVersion in pluginCrossBuild).value, scalaBinaryVersion.value)
    )
  )
  .dependsOn(aspectjRunner)
