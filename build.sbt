ThisBuild / scalaVersion := "3.0.0"

lazy val webpage =
  project
    .in(file("webpage"))
    .enablePlugins(ScalaJSPlugin)
    .settings(
      scalaJSUseMainModuleInitializer := true,
      libraryDependencies ++= Seq(
        ("org.scala-js" %%% "scalajs-dom" % "1.1.0")
          .cross(CrossVersion.for3Use2_13)
      )
    )
    .dependsOn(core.js)

lazy val webserver =
  project
    .in(file("webserver"))
    .settings(
      resolvers ++= Seq(
        "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
        "Sonatype OSS Snapshots s01" at "https://s01.oss.sonatype.org/content/repositories/snapshots"
      ),
      libraryDependencies ++= Seq(
        "com.h2database" % "h2" % "1.4.200",
        ("com.typesafe.akka" %% "akka-http" % "10.2.2").cross(CrossVersion.for3Use2_13),
        ("com.typesafe.akka" %% "akka-stream" % "2.6.10").cross(CrossVersion.for3Use2_13),
        "dev.zio" %% "zio" % "1.0.9",
        "dev.zio" %% "zio-interop-reactivestreams" % "1.3.5",
        "io.r2dbc" % "r2dbc-h2" % "0.8.4.RELEASE"
      ),
      Compile / resourceGenerators += Def.task {
        val source = (webpage / Compile / scalaJSLinkedFile).value.data
        val dest = (Compile / resourceManaged).value / "assets" / "main.js"
        IO.copy(Seq(source -> dest))
        Seq(dest)
      },
      run / fork := true
    )
    .dependsOn(core.jvm)

val circeVersion = "0.14.1"
lazy val core =
  crossProject(JSPlatform, JVMPlatform)
    .crossType(CrossType.Pure)
    .in(file("core"))
    .settings(
      libraryDependencies ++= Seq(
        "io.circe" %%% "circe-generic" % circeVersion,
        "io.circe" %%% "circe-parser" % circeVersion
      )
    )

assembly / assemblyMergeStrategy := {
  case PathList("META-INF", "MANIFEST.MF") => MergeStrategy.discard
  case _ => MergeStrategy.first
}
dockerExposedPorts += 8080
enablePlugins(JavaAppPackaging)
enablePlugins(DockerPlugin)