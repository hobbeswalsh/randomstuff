package com.wordnik.irc

import com.wordnik.irc.plugins._
import org.jibble.pircbot._

class ScalaBot(name:String, commandChar: Char) extends PircBot {
  this.setName(name)     // set our IRC name

  //val logger = new MongoLoggerPlugin
  //logger.start()

/*  val urlWatcher = new URLWatcherPlugin
  urlWatcher.start()*/


  override def onInvite(targetNick:String, sourceNick:String, sourceLogin:String, sourceHostname: String, channel:String) {
    // called when we are invited to a channel
    if ( targetNick == this.getName ) {
      if ( channel.contains(" ") ) {
        val chan = channel.split(" ").last
        this.joinChannel(chan)
      } else {
        this.joinChannel(channel)
      }
    }
  }

  override def onMessage(channel:String, sender:String, login:String, hostname:String, message:String) {
    // called whenever we see a public message in a channel
    if ( this.isCommand(message) ) {
      val c = parseCommand(message)
      val commander = new Commander(this, channel, sender, c)
      commander.start()
      commander ! c

    } else {
      // do whatever we need to do on a non-command message.
      // I imagine this will get more intricate over time.
      val m = Message(channel, sender, message)
      //val h = new Commander(this, m, channel)
      //h.start()
      
      //logger ! m

      // urlWatcher ! h // Zeke said no on this one...
    }
  }

  override def onDisconnect() {
    while ( !this.isConnected ) {
      try {
	      reconnect()
      } catch {
	      // oops! couldn't connect; sleep and try again.
	      case e: Exception => Thread.sleep(10000)
	      case _            => System.exit(1)
      }
    }
  }

  def emit(where: String, what: String) {
    if ( what.startsWith("/me" ) ) {
      this.sendAction(where, what.substring(4))
    } else {
      this.sendMessage(where, what)
    }
  }

  def isCommand(message:String): Boolean = {
    message.startsWith(this.name + ":") || message.charAt(0) == this.commandChar
  }

  def parseCommand(command:String): Command = {
    var a = command.split(" ").toList
    if ( a(0) == name + ":" ) {
      a = a.drop(1)
      new Command(a(0), a.tail)
    } else {
      new Command(a(0).substring(1), a.tail)
    }
  }
}
