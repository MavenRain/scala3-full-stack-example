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
      libraryDependencies ++= Seq(
        ("com.typesafe.akka" %% "akka-http" % "10.2.2").cross(CrossVersion.for3Use2_13),
        ("com.typesafe.akka" %% "akka-stream" % "2.6.10").cross(CrossVersion.for3Use2_13)
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
