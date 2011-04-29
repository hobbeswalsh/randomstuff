package com.wordnik.irc.plugins

class MonkeyPlugin extends GenericPlugin {
  override def go(command:List[String]): List[String] = {
    return List("EEK EEK")
  }

  def act {
  }
}
