package com.wordnik.irc.plugins

class PropsPlugin extends GenericPlugin {
  override def help() = { "?props <target> - Give someone or something a nice pat on the back." }

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
    "/me adds %s to his 'favorite word' list.",
    "/me likes %s the way Mark likes Rooster sauce.",
    "if you look up 'righteous' in the dictionary you'll find a picture of %s.",
    "my affinity for %s exceeds Tony's affinity for mints."
  )
  
  val undirected = List ( "/me pats himself on the back." )

  override def process(args:List[String]): List[String] = {
    if ( args.isEmpty ) {
      undirected
    }
    else {
      val target = args.mkString(" ")
      List(r.shuffle(directed).head.mkString.format(target))
    }
  }

}
