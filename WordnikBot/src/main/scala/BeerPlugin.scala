package com.wordnik.irc.plugins

import com.wordnik.irc._
import scala.io.Source
import scala.util.Random
import net.liftweb.json._
import net.liftweb.json.JsonParser._

// Case classes for the JSON we get back from the BeerDB API
case class BeerCount(count: Int)
case class BeerList(beers: List[Beer])

case class Beer(
  id: String,
  brewery_id: String,
  name: String,
  cat_id: String,
  style_id: String,
  abv: String,
  ibu: String,
  srm: String,
  upc: String,
  filepath: String,
  descript: String,
  add_user: String,
  last_mod: String
)

case class Brewery(
  id: String,
  name: String,
  address1: String,
  address2: String,
  city: String,
  state: String,
  code: String,
  country: String,
  phone: String,
  website: String,
  filepath: String,
  descript: String,
  add_user: String,
  last_mod: String
)


// The actual plugin
class BeerPlugin extends GenericPlugin {
  implicit val formats = DefaultFormats  // For casting JSON to case classes
  val beerUrl    = "http://obdb-dev-hoke.apigee.com/beers/get?id="
  val breweryUrl = "http://obdb-dev-hoke.apigee.com/breweries/get?id="
  val countUrl   = "http://obdb-dev-hoke.apigee.com/beers/count"
  val r = new Random

  val sayings = List(
    "/me could go for a ",
    "I feel like a ",
    "I'm going to Draegers; should I pick up some ",
    "/me thirsts for ",
    "Is it Friday yet? I want a bottle of "
  )

  def getBeer: List[String] = {
    val bc = parse(Source.fromURL(countUrl).mkString).extract[BeerCount]
    val randomNum = r.nextInt(bc.count)
    val j = Source.fromURL(beerUrl + randomNum).mkString
    val beerz = parse("{ \"beers\": " + j + "}").extract[BeerList]
    val beer = beerz.beers(0)
    val brewery = parse(Source.fromURL(breweryUrl + beer.brewery_id).mkString).extract[Brewery]
    val saying = r.shuffle(sayings).head
    List( saying + beer.name + " from " + brewery.name )
  }
  
  def act {
    loop {
      receive {
      case h: com.wordnik.irc.Hermes =>
	h.getCommand.name match {
	  case "beer" => h ! getBeer
	  case _      => None
	}
      case _  =>
	println("got somthing I didn't recognize")
	sender ! None
      }
    }
  

  }

}
