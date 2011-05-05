package com.wordnik.irc.plugins

class PropsPlugin extends GenericPlugin {
  val r = new scala.util.Random
  val directed = List(
    "/me sings a power-ballad about %s.",
    "/me writes an epic poem about %s.",
    "/me thinks that %s is pretty darn cool.",
    "/me nominates %s for the Nobel Prize in Awesome.",
    "/me recognizes the intrinsic value of %s.",
    "/me puts 50 cents in %s's tip jar.",
    "/me and %s are like, totally BFF.",
    "/me sings %s's accolades from the highest mountaintops.",
    "/me gives %s a cookie.",
    "/me defers to %s's linguistic prowess.",
    "/me consults %s when he can't find a dictionary.",
    "/me can't wait to grow up and be more like %s.",
    "/me adds %s to his 'favorite word' list."
  )
  
  val undirected = List ( "/me pats himself on the back." )

  def act {
    loop {
      receive {
	case h: com.wordnik.irc.Hermes =>
	  if ( h.getCommand.args.isEmpty ) {
	    h ! List(r.shuffle(undirected).head)
	  } else {
	    val target = h.getCommand.args.mkString(" ")
	    val reply = format(r.shuffle(directed).head.mkString, target)
	    h ! List(reply)
	  }
	case _ => sender ! None
      }
    }
  }

}
