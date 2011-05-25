package com.wordnik.irc.plugins

/*
 * -rwalsh (via IntelliJ)
 */

class URLWatcherPlugin extends GenericPlugin {
   val url_re = """http[s]?://[\S]+""".r

  def tinify(text: String): List[String] = {
    val found = url_re.findAllIn(text).toList
    if ( found.isEmpty ) { Nil }
    val tinifier = new TinyPlugin
    val ret = for { url <- found; if url.length() > 45 } yield tinifier.tinify(url)
    ret
  }

  def act() {
    loop {
      receive {
      // This is HellaBusted. I'm using a Hermes, which is currently only built to pass Commands.
      // I'll need to make Hermes more
      case h: com.wordnik.irc.Hermes =>
	h ! tinify(h.getCommand.name)
      case _  =>
	println("got somthing I didn't recognize")
	sender ! None
      }
    }
  }
}
