organization in ThisBuild := "ca.vgorcinschi"
version in ThisBuild := "1.0-SNAPSHOT"

// the Scala version that will be used for cross-compiled libraries
scalaVersion in ThisBuild := "2.13.0"

val macwire = "com.softwaremill.macwire" %% "macros" % "2.3.3" % "provided"
val scalaTest = "org.scalatest" %% "scalatest" % "3.1.1" % Test

lazy val `qanda-user` = (project in file("."))
  .aggregate(`qanda-user-api`, `qanda-user-impl`, `qanda-user-stream-api`, `qanda-user-stream-impl`)

lazy val `qanda-user-api` = (project in file("qanda-user-api"))
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslApi
    )
  )

lazy val `qanda-user-impl` = (project in file("qanda-user-impl"))
  .enablePlugins(LagomScala)
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslPersistenceCassandra,
      lagomScaladslKafkaBroker,
      lagomScaladslTestKit,
      macwire,
      scalaTest
    )
  )
  .settings(lagomForkedTestSettings)
  .dependsOn(`qanda-user-api`)

lazy val `qanda-user-stream-api` = (project in file("qanda-user-stream-api"))
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslApi
    )
  )

lazy val `qanda-user-stream-impl` = (project in file("qanda-user-stream-impl"))
  .enablePlugins(LagomScala)
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslTestKit,
      macwire,
      scalaTest
    )
  )
  .dependsOn(`qanda-user-stream-api`, `qanda-user-api`)
