package com.wordnik.irc.plugins

import scala.actors.Actor

trait GenericPlugin extends Actor {

  def help(): String = {
    "Not implemented yet - bug Robin about it!"
  }

  def process(args: List[String]): List[String] = {
    println("This plugin hasn't even been written yet! Bug Robin about it!")
    List()
  }

  def act() {
    loop {
      receive {
	      case c: com.wordnik.irc.Command =>
          reply(process(c.args))
	      case _ =>
          println("%s couldn't make sense of what it got".format(this))
	        None
      }
    }
  }

  override def exceptionHandler = {
    case e: Exception =>
      println(e.getMessage)
      sender ! List("Hmmm... something seems to have gone wrong.")
  }

}

trait LoggingPlugin extends Actor

/*
 * None of this suff works yet, but I want to have it here in case I decide to
 * break up the functionality of the interfaces the way I did with PyBo
 */

trait CommandWatcher 

trait MessageWatcher

trait JoinWatcher

