import sbt._

class WordnikBotDependencies (info: ProjectInfo) extends DefaultProject(info) {
  val classutil = "org.clapper" %% "classutil" % "0.3.4"
  val http = "org.apache.httpcomponents" % "httpclient" % "4.1.+"
  val lift = "net.liftweb" % "lift-json_2.8.1" % "2.2"
  val specs = "org.specs" % "specs" % "1.4.3"
  val argot = "org.clapper" %% "argot" % "0.2"
  val grizzled = "org.clapper" %% "grizzled-scala" % "1.0.3"
  val grizzledslf4j = "org.clapper" %% "grizzled-slf4j" % "0.4"
  val mail = "javax.mail" % "mail" % "1.4"
  val casbah = "com.mongodb.casbah" %% "casbah" % "2.1.2"
  val pircbot = "pircbot" % "pircbot" % "1.4.2"
  val snakeyaml = "org.yaml" % "snakeyaml" % "1.7"
  val scalatest = "org.scalatest" % "scalatest" % "1.3"


}
