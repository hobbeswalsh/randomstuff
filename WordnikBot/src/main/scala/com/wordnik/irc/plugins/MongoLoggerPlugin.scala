package com.wordnik.irc.plugins

import com.mongodb.casbah.Imports._
import collection.mutable.HashMap
import com.mongodb.casbah.commons.MongoDBObject

class MongoPlugin {
  val conn = MongoConnection()("irc")("log")
}

class LogSearcherPlugin extends MongoPlugin with GenericPlugin {

  def findMessages(args:List[String]): List[String] = {
    val author = args.head
    val pattern = args.tail.mkString(" ").r
    val query = MongoDBObject("nick" -> author, "message" -> pattern)
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

class ChatterPlugin extends MongoPlugin with GenericPlugin {
  def findChatterers(args:List[String]): List[String] = {
    val nicks = conn.distinct("nick")
    var nickMap = new HashMap[String, Int]()
    for ( nick <- nicks ) {
      val query = MongoDBObject("nick" -> nick)
      val messages = conn.count(query)
      nickMap += (nick.toString -> messages.toInt)
    }
    val l = nickMap.toList sortBy {_._2}
    var result = List[String]()
    val winner = l.reverse.head
    result ::= "%s: STFU already!".format(winner._1)
    for ( person <- l.takeRight(3) ) {
      result ::= "%s:  %d".format(person._1, person._2)
    }
    // the following line needs to do the right thing
    result
  }
  def act {
    loop {
      receive {
      case h: com.wordnik.irc.Hermes =>
	      h ! findChatterers(h.getCommand.args)
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
      "nick"     -> author,
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
