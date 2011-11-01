package com.wordnik.irc.plugins

import com.wordnik.irc._
import scala.io.Source
import scala.util.Random
import net.liftweb.json._
import net.liftweb.json.JsonParser._
import com.wordnik.swagger.runtime.common.APIInvoker



class QuotePlugin extends GenericPlugin {
  
  override def help() = { "?quote will get you a stock quote." }
  val baseUrl = "http://download.finance.yahoo.com/d/quotes.csv?s=%s&f=nsl1op&e=.csv"

  
  def getQuote(stocks:String): List[String] = {
    val url = baseUrl.format(stocks)
    val lines = Source.fromURL(url).mkString.split("\n")
    var ret = List[String]()
    for ( line <- lines ) {
      val fields = line.split(",")
      val now = augmentString(fields(2).replace("\"", "")).toFloat
      val yesterday = augmentString(fields(4).replace("\"", "")).toFloat
      val change = ((now - yesterday) / yesterday) * 100
      val thisQuote = "**%s** Current: %s, At open: %s, Yesterday's close: %s (%s)".format(fields(0).replace("\"", ""), fields(2), fields(3), fields(4), change)
      ret ::= thisQuote
    }
    return ret
  }
  override def process(args: List[String]): List[String] = {
    val stocks = args.mkString(",")
    getQuote(stocks)
  }

}
