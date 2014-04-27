name := "bsk-dac"

version := "1.0-SNAPSHOT"

resolvers += "Sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  "org.postgresql" % "postgresql" % "9.3-1100-jdbc4",
  "com.typesafe.play" %% "play-slick" % "0.6.0.1"
)

play.Project.playScalaSettings
