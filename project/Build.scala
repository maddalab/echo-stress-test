import sbt._
import Keys._

object EchoStressTest extends Build {
  lazy val echostresstest = Project(
    id = "echo-stress-test",
    base = file(".")
  )
}

