package com.wordnik.irc.plugins

class MonkeyPlugin extends GenericPlugin {
  val r = new scala.util.Random

  val replies = List(
    "EEK EEK!",
    "/me hides raisins about the premises",
    "/me puts on a fez",
    "/me eats a banana"
  )

  def act {
    loop {
      receive {
	case h: com.wordnik.irc.Hermes =>
	  h ! List(r.shuffle(replies).head)
	case _ => sender ! None
      }
    }
  }
}

