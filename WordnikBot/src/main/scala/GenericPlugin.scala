package com.wordnik.irc.plugins

import scala.actors.Actor

trait CommandWatcher

trait MessageWatcher

trait JoinWatcher


trait GenericPlugin extends Actor {
  def go(command:List[String]): List[String] = {
    println("You forgot to override the 'go' method...")
    Nil
  }
}
