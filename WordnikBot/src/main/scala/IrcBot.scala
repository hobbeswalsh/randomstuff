package com.wordnik.irc

import com.wordnik.irc.plugins._
import java.util.HashMap
import org.jibble.pircbot._
import scala.actors.Actor
import scala.actors._

class Command(commandline:String, channel:String)

class Replier(irc:PircBot) extends Actor {
  var command = ""
  def act {
    loop {
      react {
	case _ => println("no dice")
      }
    }
  }
}

class ScalaBot(name:String) extends PircBot {
  this.setName(name)     // set our IRC name
  var r = new Replier(this)
  r.start
  
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
  
  def waitAndSend(c: String, f: Future[Any]) {
    while ( ! f.isSet ) {
      Thread.sleep(100)
    }
    this.sendMessage(c, f().toString)
  }
  override def onMessage(channel:String, sender:String, login:String, hostname:String, message:String) {
    // called whenever we see a public message in a channel
    if ( this.isCommand(message) ) {
      println( "COMMAND RECIEVED: " + message )
      val command = parseCommand(message)
      val p = PluginFinder.find(command(0)).get
      //p.go(command).foreach( this.sendMessage(channel, _))
      //p.go(command).foreach( println(_) )
      p.start
      r.command = message
      r ! "jesus fucking christ"
      p ! r
      
//      c.send(command, OutputChannel)
    } else {
      println("Got a non-command message: " + message)
    }
  }
   

  

}

object PluginFinder {
  var m = Map[String, GenericPlugin]( "beer" -> new BeerPlugin )
  m += "monkey" -> new MonkeyPlugin
  

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
