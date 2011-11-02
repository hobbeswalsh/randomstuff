package com.wordnik.irc.plugins

import com.wordnik.irc._
import scala.collection.mutable.Map

object RPSPlugin {
  var wins = Map[String, Int]()
  var losses = Map[String, Int]()
}

class RPSPlugin extends GenericPlugin {

  def companion = RPSPlugin

  val choices = List("rock", "paper", "scissors")
  val r = new scala.util.Random


  def leftBeatsRight(left: String, right: String): Boolean = {
    left match {
      case "rock" => right match {
	case "scissors" => return true
	case "paper"    => return false
      }
      case "paper" => right match {
	case "rock" => return true
	case "scissors" => return false
      }
      case "scissors" => right match {
	case "paper" => return true
	case "rock"  => return false
      }
    }
  }
  
  def playRps(choice:String): List[String] = {
    var ret = List[String]()
    if ( ! choices.contains(choice) ) {
      return List("Yeah. It's called ROCK, PAPER, SCISSORS. So choose one of those things I just mentioned.")
    }
    val myChoice = r.shuffle(choices).head
    if ( myChoice == choice ) {
      return List("I threw %s. Looks like a draw, pardner.".format(myChoice))
    }
    if ( leftBeatsRight(myChoice, choice) ) {
      val wins = companion.wins.get(this.caller).getOrElse(0) + 1
      companion.wins += (this.caller -> wins)
      val losses = companion.losses.get(this.caller).getOrElse(0)
      var comment = "meh."
      if ( wins > losses ) {
	comment = "w00t."
      } else if ( losses > wins ) {
	comment = "harumph."
      }
      ret ::= "Sucka! Scyb0rg's %s FTW! %s %s, scyb0rg %s. %s".format(myChoice, this.caller, losses, wins, comment)
    }

    else {
      val losses = companion.losses.get(this.caller).getOrElse(0) + 1
      companion.losses += (this.caller -> losses)
      val wins = companion.wins.get(this.caller).getOrElse(0)
      ret ::= "Drat! Next time I won't throw %s. %s %s, scyb0rg %s".format(myChoice, this.caller, losses, wins)
    }

    ret

  }

  override def process(args:List[String]): List[String] = {
    if ( args.isEmpty ) {
      List("The game is called Rock, Paper, Scissors. So.... you have to choose one.")
    }
    else {
      playRps(args.head.toLowerCase())
    }
  }
}
