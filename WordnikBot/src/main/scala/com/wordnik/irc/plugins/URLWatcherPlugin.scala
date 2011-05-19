package com.wordnik.irc.plugins

/**
 * Created by IntelliJ IDEA.
 * User: rwalsh
 * Date: 5/17/11
 * Time: 4:57 PM
 * To change this template use File | Settings | File Templates.
 */

class URLWatcherPlugin extends GenericPlugin {
   val url_re = """http[s]?://[\S]+""".r

  def tinify(text: String): List[String] = {
    val found = url_re.findAllIn(text).toList
    if ( found.isEmpty ) { Nil }
    val tinifier = new TinyPlugin
    val ret = for { url <- found; if url.length() > 30 } yield tinifier.tinify(url)
    ret
  }

  def act() {
    loop {
      receive {
      case h: com.wordnik.irc.Hermes =>
	h ! tinify(h.getCommand.name)
      case _  =>
	println("got somthing I didn't recognize")
	sender ! None
      }
    }
  }
}
