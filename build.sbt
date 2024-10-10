name := "domain-parser"
version := "0.1"
scalaVersion := "2.12.15"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % "3.1.2" % "provided"
)

// Add sbt-assembly plugin
addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "1.1.0")

// Assembly settings
assembly / assemblyMergeStrategy := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x => MergeStrategy.first
}

// Ensure resources are included
Compile / unmanagedResourceDirectories += baseDirectory.value / "src" / "main" / "resources"
