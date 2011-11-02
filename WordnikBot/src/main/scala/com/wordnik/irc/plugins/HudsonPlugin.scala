package com.wordnik.irc.plugins

import com.wordnik.irc._
import scala.io.Source


class JenkinsPlugin extends GenericPlugin {

  override def help() = { "This plugin will build the project or projects you ask it to." }

  val jenkinsUrl    = "https://ci.wordnik.com/jenkins/job/%s"


  def build(projects: List[String]): List[String] = {
    var ret = List[String]()
    for ( project <- projects ) {
      val webUrl   = jenkinsUrl.format(project)
      
      val buildUrl = webUrl + "/build"
      val returnedHtml = Source.fromURL(buildUrl)
      ret ::= "Building %s at %s".format(project, webUrl)
    }
    ret.reverse
  }

  override def process(args: List[String]): List[String] = {
    build(args)
  }

}
