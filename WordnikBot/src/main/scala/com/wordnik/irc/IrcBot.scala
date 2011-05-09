package com.wordnik.irc

import org.clapper.argot._
import com.wordnik.irc.plugins._
import org.jibble.pircbot._
import scala.actors._

case class Command(
  name: String,
  args: List[String]
)

class Hermes(irc:ScalaBot, command:Command, channel:String) extends Actor {

  def getCommand: Command =  { return this.command }
  def send(s:String) {
    if ( s.startsWith("/me ") ) {
      this.irc.sendAction(channel, s.substring(4))
    } else {
      this.irc.sendMessage(channel, s)
    }
  }
  def act {
    loop {
      react {
	case l: List[String] => 
	  l.foreach(send(_))
	case other => println("no dice")
      }
    }
  }
}

case class Message(channel:String, author:String, text:String)


class ScalaBot(name:String) extends PircBot {
  this.setName(name)     // set our IRC name
  val logger = new MongoLoggerPlugin
  logger.start

  val commandChar = '?'
  // the below matches "?<command>" or "<name>: command" 
  val m = format("""(?:^\%s|^%s:[ ]?)""", commandChar, name, name).r

  def isCommand(message:String): Boolean = {
    return message.startsWith(this.getName + ":") || message.charAt(0) == this.commandChar
  }
  
  def parseCommand(command:String): List[String] = {
    val a = command.split(" ").toList
    if ( a(0) == name + ":" ) {
      println(a)
      println(a.tail)
      return a.tail
    } else {
      return a(0).substring(1) :: a.tail
    }
  }
  
  override def onDisconnect {
    while ( !this.isConnected() ) {
      try {
	reconnect
      } catch {
	// oops! couldn't connect; sleep and try again.
	case e: Exception => Thread.sleep(10000)
	case _            => System.exit(1)
      }
    }
  }

  override def onInvite(targetNick:String, sourceNick:String, sourceLogin:String, sourceHostname: String, channel:String) {
    // called when we are invited to a channel
    if ( targetNick == this.getName ) {
      this.joinChannel(channel)
    }
  }

  override def onMessage(channel:String, sender:String, login:String, hostname:String, message:String) {
    // called whenever we see a public message in a channel
    if ( this.isCommand(message) ) {
      val parsedMessage = parseCommand(message)
      val command = new Command(parsedMessage(0), parsedMessage.tail)

      if ( PluginFinder.find(command.name) == None ) { return None }
      val plugin =  PluginFinder.find(command.name).get

      // Make a new Hermes to deliver this message
      val h = new Hermes(this, command, channel)
      h.start
      
      plugin.start          // Every pluign is an actor       
      plugin ! h            // tell the plugin actor to process Hermes' message
      
    } else {
      // do whatever we need to do on a non-command message.
      // I imagine this will get more intricate over time.
      logger ! Message(channel, sender, message)
    }
  }

}

object PluginFinder {
  var m = Map[String, GenericPlugin](
    "beer"    -> new BeerPlugin,
    "monkey"  -> new MonkeyPlugin,
    "address" -> new AddressPlugin,
    "diss"    -> new DissPlugin,
    "props"   -> new PropsPlugin,
    "fortune" -> new FortunePlugin,
    "tiny"    -> new TinyPlugin,
    "said"    -> new LogSearcherPlugin,
    "better"  -> new BetterPlugin
  )
  

  def find(name:String): Option[GenericPlugin] = {
    m.get(name)
  }

}



object ScalaBot {
  import ArgotConverters._
  val parser = new ArgotParser("ScalaBot")
  val nickname = parser.option[String](
    List("n", "nickname"),
    "username",
    "Bot's Nickname")

  val server = parser.option[String](
    List("s", "server"),
    "server",
    "The IRC Server to connect to")

  def main(args:Array[String]) {
    parser.parse(args)
    val b = new ScalaBot(nickname.value.getOrElse("ScalaBot"))
    // b.setVerbose(true)
    b.connect(server.value.getOrElse("irc.freenode.net"))
  }
}
