package com.wordnik.ircbot

import f00f.net.irc.martyr._

class IRCBot(name: String="scabo", server: String="irc.freenode.net", port: Int=6667) {
  val state = new clientstate.ClientState()
  state.setServer(server)
  state.setNick(new util.FullNick(name))
  state.setName(name)
  val connection = new IRCConnection( state )
  val autoReg = new services.AutoRegister(connection, name, name, name )
  val autoRecon = new services.AutoReconnect( connection )
  val autoRes = new services.AutoResponder( connection )

  def go: Any = {
    autoRecon.go(server, port)
  }
}

object BotRunner {
  def main(args:Array[String]): Any = {
    val bot = new IRCBot()
    bot.go
    

  }
}
