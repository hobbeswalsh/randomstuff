package com.wordnik.irc.plugins

import util.Random
import net.liftweb.json.DefaultFormats
import net.liftweb.json.JsonParser._
import scala.io.Source
import java.net. {URLEncoder, URLDecoder}

import xml.parsing.ConstructingParser

/*
 * -rwalsh (via IntelliJ) 
 */

/*
{
  "offset": "0",
  "results": [
    {
      "body": "LEAD: SATELLITE monitoring shows that Antarctica has shed more than 11,000 of its 5 million square miles of ice since the 1970's, and scientists say further study is needed to determine whether the findings indicate that global warming is melting polar ice. SATELLITE monitoring shows that Antarctica has shed more than 11,000 of its 5 million square",
      "byline": "By WALTER SULLIVAN",
      "date": "19900814",
      "title": "Antarctica Sheds Ice and Scientists Wonder Why",
      "url": "http:\/\/www.nytimes.com\/1990\/08\/14\/science\/antarctica-sheds-ice-and-scientists-wonder-why.html"
    },
    {
 */

case class NYTResults ( offset: String, results: List[NYTResult] )
case class NYTResult ( body: String, byline: String, date: String, title: String, url: String )

class NYTimesPlugin extends GenericPlugin {

  override def help() = { "This will search for articles containing your query and return one of the results." }

  val r = new Random
  val key = "4eda2bce953f2bc71529351dd028db8b:3:62536233" // this is stupid!
  val baseUrl = "http://api.nytimes.com/svc/search/v1/article?format=json&api-key=%s&query=".format(key)

  implicit val formats = DefaultFormats  // For casting JSON to case classes

  def search(query:String): List[String] = {
    val nyturl = baseUrl + query
    val result = parse(Source.fromURL(nyturl).mkString).extract[NYTResults]
    val choice =  r.shuffle(result.results).head
    val repl = "%s (%s) -> %s".format(choice.title, choice.byline, choice.url)
    List(repl)
  }

  override def process(args:List[String]): List[String] = {
    search(args.mkString("+"))
  }
}

// http://api.nytimes.com/svc/search/v1/article?format=json&query=lakes+ice&rank=closest&api-key=####