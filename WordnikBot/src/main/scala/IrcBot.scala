package com.wordnik.irc

import com.wordnik.irc.plugins._
import java.util.HashMap
import org.jibble.pircbot._
import scala.actors.Actor
import scala.actors._

case class Command(
  name: String,
  args: List[String]
)

class Hermes(irc:PircBot, command:Command, channel:String) extends Actor {

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


class ScalaBot(name:String) extends PircBot {
  this.setName(name)     // set our IRC name


  val commandChar = "?"
  // the below matches "?<command>" or "<name>: command" 
  val m = format("""(?:^\%s|^%s:[ ]?)""", commandChar, name, name).r

  def isCommand(message:String): Boolean = {
    return message.startsWith(this.getName + ":") || message.startsWith(this.commandChar)
  }
  
  def parseCommand(command:String): List[String] = {
    val a = command.split(" ").toList
    return a(0).substring(1) :: a.tail
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
      val command = parseCommand(message)
      val plugin = PluginFinder.find(command(0)).get

      if ( plugin == None ) { return None }
      
      val c = new Command(command(0), command.tail)

      // Make a new Hermes to deliver this message
      val h = new Hermes(this, c, channel)
      h.start
      
      plugin.start          // Every pluign is an actor       
      plugin ! h            // tell the plugin actor to process the Replier message
      
    } else {
      println("Got a non-command message: " + message)
    }
  }

}

object PluginFinder {
  var m = Map[String, GenericPlugin]( 
       "beer"    -> new BeerPlugin,
       "monkey"  -> new MonkeyPlugin,
       "address" -> new AddressPlugin,
       "better"  -> new BetterPlugin
  )
  

  def find(name:String): Option[plugins.GenericPlugin] = {
    val foo = m.get(name)
    if ( foo.isEmpty ) { return None }
    else { return foo }
  }

}
object Main {
  def main(args:Array[String]) {
    val b = new ScalaBot("hobbeswalsh11")

    b.setVerbose(true)

    //b.onMessage("#foo", "bar", "bar", "bar", "?beer")
    b.connect("irc.freenode.net")

    //System.exit(0)
  }
}
