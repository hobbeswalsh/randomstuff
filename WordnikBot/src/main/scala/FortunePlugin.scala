package com.wordnik.irc.plugins

import scala.io.Source
import scala.util.Random
import net.liftweb.json._
import net.liftweb.json.JsonParser._

case class Fortune(
  json_class: String,
  tags: List[String],
  quote: String,
  link: String,
  source: String
)

class FortunePlugin extends GenericPlugin {

  implicit val formats = DefaultFormats  // For casting JSON to case classes

  val url = "http://www.iheartquotes.com/api/v1/random?format=json"

  def getFortune: List[String] = {
    val fortune = parse(Source.fromURL(url).mkString).extract[Fortune]
    fortune.quote.split("\n").toList
  }

  def act {
    loop {
      receive {
      case h: com.wordnik.irc.Hermes =>
	h ! getFortune
      case _  =>
	println("got somthing I didn't recognize")
	sender ! None
      }
    }
  }
}
