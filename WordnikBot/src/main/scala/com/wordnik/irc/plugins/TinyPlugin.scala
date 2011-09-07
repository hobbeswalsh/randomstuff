package com.wordnik.irc.plugins

import scala.io.Source

class TinyPlugin extends GenericPlugin {

  val baseurl = "http://tinyurl.com/api-create.php?url="

  def tinify(url:String): String = {
    var u = url
    if ( ! (u.startsWith("http://") || u.startsWith("https://")) ) {
      u = "http://" + u
    }
    Source.fromURL(baseurl + u).mkString
  }

  override def process(args:List[String]): List[String] = {
    args.map(tinify(_)).toList
  }
}
