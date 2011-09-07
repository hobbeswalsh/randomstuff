package com.wordnik.irc

/*
 * -rwalsh (via IntelliJ) 
 */

case class Message(channel:String, author:String, text:String) {
  var isCommand = false
  def getCommand: Command = {

    if ( this.isCommand == false ) {
      return new Command("", Nil)
    }

    var a = this.text.split(" ").toList

    if ( a(0).contains(':')  ) {
      // matching List("scyb0rg:", "diss", "robin")
      a = a.drop(1)
      new Command(a(0), a.tail)
    }
    else {
      // matching List("?diss", "robin")
      new Command(a(0).substring(1), a.tail)
    }
  }
}

