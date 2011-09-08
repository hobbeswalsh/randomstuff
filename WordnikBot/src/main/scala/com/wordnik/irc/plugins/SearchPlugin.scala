package com.wordnik.irc.plugins

import util.Random
import net.liftweb.json.DefaultFormats
import scala.io.Source
import net.liftweb.json.JsonParser._

/*
 * -rwalsh (via IntelliJ) 
 */

case class BlekkoResult (
  DYM: DymObject,
  ERROR: ErrorObject,
  RESULT: List[BlekkoSearchResult]
                          )
case class BlekkoSearchResult (
  c: Int,
  display_url: String,
  n_group: Int,
  short_host: String,
  short_host_url: String,
  snippet: String,
  url: String,
  url_title: String
                                )
case class DymObject()
case class ErrorObject()

class SearchPlugin extends GenericPlugin {
   override def help() = { "Web search!" }

  val r = new Random
  val key = "b425e57b" // this is stupid!
  val baseUrl = "http://blekko.com/ws/?auth=%s&q=".format(key)

  implicit val formats = DefaultFormats  // For casting JSON to case classes

  def search(query:String): List[String] = {
    val nyturl = baseUrl + query + "+/json"
    val result = parse(Source.fromURL(nyturl).mkString).extract[BlekkoResult]
    val choice =  r.shuffle(result.RESULT).head
    val snippet = choice.snippet
    val url = choice.url
    List("%s (%s)".format(snippet, new TinyPlugin().tinify(url)))
  }

  override def process(args:List[String]): List[String] = {
    search(args.mkString("+"))
  }


}