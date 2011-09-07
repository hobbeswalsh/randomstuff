package com.wordnik.irc.plugins

import net.liftweb.json.JsonParser._
import scala.io.Source
import java.net. {URLEncoder, URLDecoder}
import scala.util.Random
import net.liftweb.json.DefaultFormats
import xml.parsing.ConstructingParser


/*
 * -rwalsh (via IntelliJ) 
 */

case class TwitterSearchResults( results: List[TwitterSearchResult] )
case class TwitterSearchResult (
  created_at:   String,
  text:         String,
  metadata:     TwitterResultMetaData,
  id:           Int,
  id_str:       String,
  from_user_id: Int,
  from_user:    String,
  source:       String )

case class TwitterResultMetaData( result_type: String )

class TwitterSearchPlugin extends GenericPlugin {

  val r = new Random
  val baseUrl = "http://search.twitter.com/search.json?rpp=100&q="
  implicit val formats = DefaultFormats  // For casting JSON to case classes

  def search(query:String): List[String] = {
    val urlEncodedQuery = URLEncoder.encode(query, "UTF-8")
    val twUrl = baseUrl + urlEncodedQuery
    val result = parse(Source.fromURL(twUrl).mkString).extract[TwitterSearchResults]
    val choice =  r.shuffle(result.results).head
    val refUrl = "http://twitter.com/#!/%s/status/%s".format(choice.from_user, choice.id_str)
    // this is hella stupid
    val d = ConstructingParser.fromSource(Source.fromString("<dummy>%s</dummy>".format(choice.text)), true).document()
    val tweet = d(0).text
    val repl = "%s says: \"%s\" (%s)".format(choice.from_user, tweet, refUrl)
    List(repl)
  }

  override def process(args:List[String]): List[String] = {
    search(args.mkString(" "))
  }
}

// http://search.twitter.com/search.json?q=%23yahoo