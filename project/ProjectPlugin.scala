package sbt.com.perikov.cql

import sbt._
import Keys._

object ProjectPlugin extends AutoPlugin {
  override def trigger = allRequirements

  override lazy val projectSettings = Seq(
    scalaVersion := "3.4.2",
    organization := "com.perikov",
    version := "0.1.0-SNAPSHOT",
    scalacOptions ++= Seq(
      "-deprecation",
      "-feature",
      "-unchecked",
      "-Wunused:all",
      "-Xsource:future-migration",
      "-Yexplicit-nulls",
      "-Ykind-projector",
      "-language:strictEquality",
      "-explain",
      "-rewrite"
    ),
    Compile / scalaSource := baseDirectory.value / "src"
  )
}
