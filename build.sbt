
ThisBuild / version := "0.1.0-SNAPSHOT"

lazy val root = (project in file("."))
  .settings(
    name := "theorg",
    scalaVersion := "2.12.14",
    Compile / scalacOptions += "-Xlint",
    Compile / console / scalacOptions --= Seq("-Ywarn-unused", "-Ywarn-unused-import"),
    scalacOptions --= Seq("-Ywarn-unused", "-Ywarn-unused-import"),
    Test / fork := true,
    libraryDependencies ++= Seq(
      "org.scalatestplus" %% "selenium-3-141" % "3.2.10.0",
      "org.jsoup" % "jsoup" % "1.15.3",
      "io.github.bonigarcia" % "webdrivermanager" % "5.3.2",
      "dev.zio" %% "zio" % "2.0.6",
      "com.github.ghostdogpr" %% "caliban-zio-http" % "2.0.2",
      "org.mongodb" % "mongodb-driver-sync" % "4.8.2",
      "org.slf4j" % "slf4j-log4j12" % "2.0.6"
    )

  )

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", _*) => MergeStrategy.discard
  case _                        => MergeStrategy.first
}


