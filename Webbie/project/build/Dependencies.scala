import sbt._

class WebbieProject(info: ProjectInfo) extends DefaultProject(info)
{
      val http = "org.apache.httpcomponents" % "httpclient" % "4.1.+"
      //val http2 = "org.apache.httpcomponents" % "httpasyncclient" % "4.0x.+"

}
