name := "mongoDB"

version := "1.1.0"

scalaVersion := "2.10.1"

scalacOptions ++= Seq("-deprecation")

EclipseKeys.withSource := true

resolvers ++= Seq("snapshots"     at "http://oss.sonatype.org/content/repositories/snapshots",
                  "releases"      at "http://oss.sonatype.org/content/repositories/releases")

libraryDependencies ++= {
  val liftVersion = "2.5-M4"
  Seq(
    "net.liftweb" %% "lift-mongodb-record" % liftVersion,
    "org.scalatest" %% "scalatest" % "2.0.M6-SNAP9" % "test",
    "org.specs2"        %% "specs2"             % "1.14"           % "test",
    "junit" % "junit" % "4.11" % "test",
    "org.mongodb" %% "casbah" % "2.5.0",
    "ch.qos.logback" % "logback-classic" % "1.0.9",
    "org.mongeez" % "mongeez" % "0.9.3",
    "org.springframework" % "spring-core" % "3.2.1.RELEASE",
    "log4j" % "log4j" % "1.2.17",
    "commons-lang" % "commons-lang" % "2.6",
    "com.foursquare" %% "rogue-field"         % "2.0.0-beta22" intransitive(),
    "com.foursquare" %% "rogue-core"          % "2.0.0-beta22" intransitive(),
    "com.foursquare" %% "rogue-lift"          % "2.0.0-beta22" intransitive(),
    "com.foursquare" % "fongo" % "1.0.7"
  )
}

