package com.wordnik.irc.plugins

import com.wordnik.irc._

/*
 * -rwalsh (via IntelliJ) 
 */

class HelperPlugin extends GenericPlugin {

  override def help() = { "Are you serious?" }

  def getHelp(args:List[String]): List[String] = {
    if ( args.isEmpty ) {
      val plugins = PluginFinder.m.keys.toArray.sorted.mkString(", ")
      val rply = "Here are the plugins I know about: " + plugins + ". Type ?help <plugin> to get more help about a particular plugin."
      List(rply)
    }
    else {
      var repl = List[String]()
      for ( command <- args ) {
        val plugin = PluginFinder.m.getOrElse(command, this)
        repl ::= plugin.help()
      }
      repl
    }
  }

  override def process(args:List[String]): List[String] = {
    getHelp(args)
  }

}
