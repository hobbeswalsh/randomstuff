package com.wordnik.irc.plugins

import scala.io.Source

class TinyPlugin extends GenericPlugin {

  val baseurl = "http://tinyurl.com/api-create.php?url="

  def tinify(url:String): String = {
    var u = url
    if ( ! u.startsWith("http://") ) {
      u = "http://" + u
    }
    Source.fromURL(baseurl + u).mkString
  }

  def act {
    loop {
      receive {
	case h: com.wordnik.irc.Hermes =>
	  val tinies = h.getCommand.args.map(tinify(_)).toList
	  h ! tinies
	case _ => sender ! None
      }
    }
  }
}

