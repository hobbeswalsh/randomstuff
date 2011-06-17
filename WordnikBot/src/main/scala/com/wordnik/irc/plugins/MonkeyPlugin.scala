package com.wordnik.irc.plugins

class MonkeyPlugin extends GenericPlugin {
  override def help() = { "?monkey will give me a chance to display my more simian side." }

  val r = new scala.util.Random

  val replies = List(
    "EEK EEK!",
    "/me hides raisins about the premises",
    "/me puts on a fez",
    "/me eats a banana",
    "/me swings to and fro from his simian tail"
  )

  def act {
    loop {
      receive {
	case h: com.wordnik.irc.Hermes =>
	  h ! List(r.shuffle(replies).head)
	case _ =>
	  None
      }
    }
  }
}

