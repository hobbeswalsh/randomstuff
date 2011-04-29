package com.wordnik.irc.plugins

import scala.actors.Actor
import scala.actors.Actor._

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

  override def go(command:List[String]): List[String] = {
    Thread.sleep(5)
    val bc = parse(Source.fromURL(countUrl).mkString).extract[BeerCount]
    val randomNum = r.nextInt(bc.count)
    val j = Source.fromURL(beerUrl + randomNum).mkString
    val beerz = parse("{ \"beers\": " + j + "}").extract[BeerList]
    val beer = beerz.beers(0)
    val brewery = parse(Source.fromURL(breweryUrl + beer.brewery_id).mkString).extract[Brewery]
    List( beer.name + " from " + brewery.name )
  }
  
  def act {
    loop { receive {
      case r: Replier =>
	println(r)
	println(sender)
	sender ! List("holy moly got a command")
	println("fucking fuck")
      // case List("beer", "drink", _*) =>
      // 	println("drankin some beer")
      // 	reply(List("hello beer"))
      // case List("beer", _*) =>
      // 	println("getting some beer")
      case _                =>
	println("other")
	sender ! "yummmmm"
	println( sender.getClass() )
      
    } }
  

  }

}
