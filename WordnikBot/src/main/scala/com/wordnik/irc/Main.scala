package com.wordnik.irc

import org.clapper.argot.{ArgotParser, ArgotConverters}

/*
* -rwalsh (via IntelliJ)
*/

object Main {

  val defaultNick         = "Scyb0rg"
  val defaultCommandChar  = '?'
  val defaultServer       = "irc.freenode.net"

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

  val commandChar = parser.option[Char](
    List("c", "commandChar"),
    "commandChar",
    "The command character the bot will use"
  )

  def main(args:Array[String]) {
    parser.parse(args)
    val b = new ScalaBot(nickname.value.getOrElse(defaultNick), commandChar.value.getOrElse(defaultCommandChar))
    // b.setVerbose(true)
    b.connect(server.value.getOrElse(defaultServer))
  }
}