package com.wordnik.irc

import scala.actors.Actor
/*
 * -rwalsh (via IntelliJ) 
 */

class Commander(bot:ScalaBot, chan:String, who:String, command:Command) extends Actor {

  def processCommand() {
    if ( PluginFinder.find(command.name) == None ) { println("no such command name: %s".format(command.name)); return }
    // Every plugin is an actor
    val plugin =  PluginFinder.find(command.name).get
    plugin.start()
    val result = (plugin !! command)
    result() match {
      case r: List[String] => sendReply(r)
      case _               => None
    }
  }

  def sendReply(l:List[String]) {
    var toWhom = chan
    if ( l.size > 3 ) {
      toWhom = who
    }
    l.foreach(bot.emit(toWhom, _))
  }

  def act() {
    loop {
      receive {
        case _ => processCommand()
      }
    }
  }

}