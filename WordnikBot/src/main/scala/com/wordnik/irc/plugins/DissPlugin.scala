package com.wordnik.irc.plugins

class DissPlugin extends GenericPlugin {

  val r = new scala.util.Random

  val directed = List(
    "/me submits a bug report about %s.",
    "/me has a word with %s's manager.",
    "/me tells a raunchy yo'-momma joke about %s.",
    "/me tweets something negative about %s.",
    "/me spits chai on %s's laptop.",
    "/me fakes a high-five with %s but pulls back at the last minute.",
    "/me says something about %s that merits an 'oh snap!'",
    "/me gives %s the cold shoulder.",
    "/me gives %s the hairy eyeball.",
    "/me mumbles under his breath about %s.",
    "/me submits a post about %s to failblog.org.",
    "/me signs a petition barring %s from public spaces.",
    "/me motions to have %s deported to Cydonia.",
    "/me takes away %s's Legos.",
    "/me messes with %s's chair.",
    "/me puts tape under %s's mouse.",
    "/me steals %s's stapler.",
    "/me repeatedly sends lewd IMs to %s's manager.",
    "/me fist-bumps %s... in the back of the head.",
    "/me creates a profile for %s on gothicmatch.com",
    "/me adds %s to his 'least favorite words' list.",
    "/me would rather play air hockey against Colin than deal with %s."
  )

  val undirected = List( "/me looks around for someone to diss." )

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
