val tapirVersion = "1.9.5"
val http4sVersion = "0.23.24"
val ironVersion = "2.3.0"

lazy val rootProject = (project in file(".")).settings(
  Seq(
    name := "subtube",
    version := "0.1.0-SNAPSHOT",
    organization := "com.jb",
    scalaVersion := "3.3.1",
    cancelable in Global := true,
    semanticdbEnabled := true,
    semanticdbVersion := scalafixSemanticdb.revision,
    libraryDependencies ++= Seq(
      "io.github.iltotore" %% "iron" % ironVersion,
      "io.github.iltotore" %% "iron-circe" % ironVersion,
      "org.http4s" %% "http4s-ember-server" % http4sVersion,
      "org.http4s" %% "http4s-ember-client" % http4sVersion,
      "org.http4s" %% "http4s-dsl" % http4sVersion,
      "org.http4s" %% "http4s-circe" % http4sVersion,
      "ch.qos.logback" % "logback-classic" % "1.4.14",
      "com.softwaremill.sttp.tapir" %% "tapir-http4s-server" % tapirVersion,
      "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-bundle" % tapirVersion,
      "com.softwaremill.sttp.tapir" %% "tapir-json-circe" % tapirVersion,
      "com.softwaremill.sttp.tapir" %% "tapir-sttp-stub-server" % tapirVersion % Test,
      "com.softwaremill.sttp.tapir" %% "tapir-iron" % tapirVersion,
      "com.softwaremill.sttp.tapir" %% "tapir-json-pickler" % tapirVersion,
      "org.scalatest" %% "scalatest" % "3.2.17" % Test,
      "com.softwaremill.sttp.client3" %% "circe" % "3.9.1" % Test,
    ),
  ),
)
