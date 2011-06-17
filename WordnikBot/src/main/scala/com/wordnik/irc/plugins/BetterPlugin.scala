package com.wordnik.irc.plugins

import scala.io.Source
import scala.util.Random
import net.liftweb.json._
import net.liftweb.json.JsonParser._

case class SucksRocksItem (
  term: String,
  sucks: Double,
  rocks: Double
)

class BetterPlugin extends GenericPlugin {

  override def help() = { "?better <item1> or <item2> or ... I'll do my research and let you know what's better." }

  implicit val formats = DefaultFormats  // For casting JSON to case classes

  val baseurl = "http://sucks-rocks.com/query?"
  
  def findBetter(args:List[String]): List[String] = {
    var scores:List[String] = List()
    val splitWord = " or ".r
    val terms = splitWord.split(args.mkString(" "))
    for ( item <- terms ) {
      val url = baseurl + "term=" + item
      val currentValue = parse(Source.fromURL(url).mkString).extract[SucksRocksItem]
      val score = currentValue.rocks / (currentValue.sucks + currentValue.rocks)
      scores :::= List(format("%-25s: %.2f", item, score))
    }
    scores
  }

  def act {
    loop {
      receive {
      case h: com.wordnik.irc.Hermes =>
	h ! findBetter(h.getCommand.args)
      case _  =>
	println("got somthing I didn't recognize")
	sender ! None
      }
    }
  

  }

}
