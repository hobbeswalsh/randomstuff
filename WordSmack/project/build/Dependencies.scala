import sbt._

import sbt._

class WebbieProject(info: ProjectInfo) extends DefaultProject(info) {
  val http = "org.apache.httpcomponents" % "httpclient" % "4.1.+"
  val lift = "net.liftweb" % "lift-json_2.8.1" % "2.2"
  val specs = "org.specs" % "specs" % "1.4.3"
  val argot = "org.clapper" %% "argot" % "0.2"
  val grizzled = "org.clapper" %% "grizzled-scala" % "1.0.3"
}

