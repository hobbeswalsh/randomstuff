package com.wordnik.irc.plugins

import scala.io.Source
import scala.util.Random
import net.liftweb.json._
import net.liftweb.json.JsonParser._


// Case classes for the JSON we get back from the BeerDB API

case class YelpCategory (
  category_filter: String,
  name: String
)

case class YelpReview (
  rating: Int,
  text_excerpt: String,
  date: String
)
case class YelpBiz (
  city: String,
  zip: String,
  state: String,
  address1: String,
  phone: String,
  categories: List[YelpCategory],
  name: String,
  avg_rating: Double,
  reviews: List[YelpReview]
)

case class YelpResult (
  businesses: List[YelpBiz]
)

/*
 * -rwalsh (via IntelliJ) 
 */

class YelpPlugin extends GenericPlugin {

  override def help() = { "?lunch will help you decide what to get for lunch." }

  implicit val formats = DefaultFormats  // For casting JSON to case classes
  
  val defaultLoc = "San Mateo CA"
  
  val r = new Random

  def getLunch(loc:String=defaultLoc): List[String] = {
    val urlEncodedLoc = loc.replace(" ", "%20")
    val yelpUrl    = "http://api.yelp.com/business_review_search?term=lunch&location=%s&ywsid=9QzogsLERlJBkSBkQoNhaQ&radius_filter=1000".format(urlEncodedLoc)
    try {
      val results = parse(Source.fromURL(yelpUrl).mkString).extract[YelpResult]
      val result = r.shuffle(results.businesses).head
      val repl = result.name + ": " + result.address1 + " (" + result.phone + ")"
      List(repl)
    }
    catch {
      case e: Exception => println(e);
      List()
    }

  }

  override def process(args:List[String]): List[String] = {
    var target = ""
    if ( args.isEmpty ) {
      target = defaultLoc
    }
    else {
      target = args.mkString(" ")
    }
    getLunch(target)
  }

}
