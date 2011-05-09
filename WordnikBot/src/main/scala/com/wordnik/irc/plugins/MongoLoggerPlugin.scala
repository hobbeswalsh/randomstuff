package com.wordnik.irc.plugins

import com.mongodb.casbah.Imports._

class MongoPlugin {
  val conn = MongoConnection()("irc")("log")
}

class LogSearcherPlugin extends MongoPlugin with GenericPlugin {

  def findMessages(args:List[String]): List[String] = {
    val author = args.head
    val pattern = args.tail.mkString(" ").r
    val query = MongoDBObject("nickname" -> author, "message" -> pattern)
    conn.find(query).map(author + ": " + _.get("message").toString).toList
  }

  def act {
    loop {
      receive {
      case h: com.wordnik.irc.Hermes =>
	  h ! findMessages(h.getCommand.args)
      case _  =>
	println("got somthing I didn't recognize")
	sender ! None
      }
    }
  } 

}

class MongoLoggerPlugin extends MongoPlugin with LoggingPlugin {

  def log(channel:String, author:String, message:String) {
    val now = compat.Platform.currentTime
    val insert = MongoDBObject(
      "message"  -> message,
      "nickname" -> author,
      "channel"  -> channel,
      "time"     -> now
    )
    conn += insert
  }

  def act {
    loop {
      receive {
      case m: com.wordnik.irc.Message =>
	  log(m.channel, m.author, m.text)
      case _  =>
	println("got somthing I didn't recognize")
	sender ! None
      }
    }
  } 

}
