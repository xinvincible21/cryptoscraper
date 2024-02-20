
ThisBuild / version := "0.1.0-SNAPSHOT"

lazy val root = (project in file("."))
  .settings(
    name := "cryptoscraper",
    scalaVersion := "2.12.14",
    Compile / scalacOptions += "-Xlint",
    Compile / console / scalacOptions --= Seq("-Ywarn-unused", "-Ywarn-unused-import"),
    scalacOptions --= Seq("-Ywarn-unused", "-Ywarn-unused-import"),
    Test / fork := true,
    assembly / assemblyMergeStrategy := {
      case PathList("META-INF", _*) => MergeStrategy.discard
      case _ => MergeStrategy.first
    },
    libraryDependencies ++= Seq(
      "org.scalatestplus" %% "selenium-4-17_2.12" % "3.2.18.0",
      "org.jsoup" % "jsoup" % "1.15.3",
      "io.github.bonigarcia" % "webdrivermanager" % "5.6.4",
      "dev.zio" %% "zio" % "2.0.6",
      "com.github.ghostdogpr" %% "caliban-zio-http" % "2.0.2",
      "org.mongodb" % "mongodb-driver-sync" % "4.8.2",
      "ch.qos.logback" % "logback-core" % "1.4.1",
      "ch.qos.logback" % "logback-classic" % "1.4.1",
      "org.slf4j" % "slf4j-api" % "2.0.7"
    )

  )


