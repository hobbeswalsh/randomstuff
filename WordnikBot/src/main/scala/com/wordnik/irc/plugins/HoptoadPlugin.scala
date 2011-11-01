package com.wordnik.irc.plugins

import util.Random
import net.liftweb.json.DefaultFormats
import net.liftweb.json.JsonParser._
import scala.io.Source

/*
 * -rwalsh (via IntelliJ) 
 */

/*
[
-{
-group: {
created_at: "2011-06-21T22:06:33Z"
notice_hash: "5ed76444dc6ff5f4ffead6b57158ac1e"
project_id: 19204
updated_at: "2011-09-14T21:14:23Z"
action: "photos"
notices_count: 164446
resolved: false
error_class: "ActionView::MissingTemplate"
error_message: "ActionView::MissingTemplate: Missing template words/photos with {:handlers=>[:erb, :rjs, :builder, :rhtml, :rxml, :haml], :formats=>[:html, :text, :js, :css, :ics, :csv, :xml, :rss, :atom, :yaml, :multipart_form, :url_encoded_form, :json], :locale=>[:en, "
id: 8358789
lighthouse_ticket_id: null
controller: "words"
file: "[GEM_ROOT]/gems/actionpack-3.0.5/lib/action_view/paths.rb"
rails_env: "production"
line_number: 15
most_recent_notice_at: "2011-09-14T21:14:23Z"
}
}
 */

case class HopToadResult( result: List[HopToadError] )
case class HopToadError( group: HopToadErrorGroup )
case class HopToadErrorGroup (
  created_at: String,
  project_id: Int,
  notices_count: Int,
  error_class: String,
  error_message: String,
  id: Int
)

class HoptoadPlugin extends GenericPlugin {

  override def help() = { "Usage: hoptoad [n] - get the last n (default 3) errors from Hoptoad" }

  val key = "132117e05320a4a75b6dfe371ad931dbbc4294c2" // this is stupid!
  val baseUrl = "https://wordnik.hoptoadapp.com/errors.json?auth_token=%s".format(key)
  val webUrl = "https://wordnik.airbrakeapp.com/errors/"

  implicit val formats = DefaultFormats  // For casting JSON to case classes

  def getErrors(num: Int=3): List[String] = {
    val json = "{ \"result\": " + Source.fromURL(baseUrl).mkString + "}"
    val results = parse(json).extract[HopToadResult].result.take(num)
    var l = List[String]()
    for ( error <- results ) {
      val eg = error.group
      val url = new TinyPlugin().tinify(webUrl + eg.id.toString)
      val msg = eg.error_class + " -- " + url
      l = msg :: l
    }
    l
  }

  override def process(args:List[String]): List[String] = {
    if ( args.isEmpty ) {
      getErrors(3)
    }
    else {
      getErrors(args.head.toInt)
    }
  }
}
