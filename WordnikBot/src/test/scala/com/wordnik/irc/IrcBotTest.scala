package com.wordnik.irc

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers

import com.wordnik.irc.plugins._
import com.wordnik.irc._

class IrcBotTest extends FlatSpec with ShouldMatchers {
  "PluginFinder" should "return the correct plugin" in {
    val p = PluginFinder.find("monkey")
    assert( p.isInstanceOf[Some[MonkeyPlugin]] )
  }
  
  "Command" should "have both a command and arguments" in {
    val c = new Command("command", List("arg1", "arg2", "arg3"))
    c.name should be === "command"
    c.args(0) should be === "arg1"
  }
  
}
