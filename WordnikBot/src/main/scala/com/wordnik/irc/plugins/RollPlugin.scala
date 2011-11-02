package com.wordnik.irc.plugins

class RollPlugin extends GenericPlugin {
  val r = new scala.util.Random

  override def help() =  { "Roll the dice!" }

  def roll(sides:Int): List[String] = {
    return List(( r.nextInt(sides) + 1).toString)
  }
  

  override def process(args:List[String]): List[String] = {
    if ( args.isEmpty ) {
      roll(6)
    }
    else {
      roll(args.head.toInt)
    }
  }

}
