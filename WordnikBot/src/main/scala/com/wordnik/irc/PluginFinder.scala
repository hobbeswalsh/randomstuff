package com.wordnik.irc

import plugins._

/*
* -rwalsh (via IntelliJ)
*/

object PluginFinder {
  var m = Map[String, GenericPlugin](
    "beer"    -> new BeerPlugin,
    "monkey"  -> new MonkeyPlugin,
    "address" -> new AddressPlugin,
    "diss"    -> new DissPlugin,
    "props"   -> new PropsPlugin,
    "fortune" -> new FortunePlugin,
    "tiny"    -> new TinyPlugin,
    "said"    -> new LogSearcherPlugin,
    "better"  -> new BetterPlugin,
    "stfu"    -> new ChatterPlugin,
    "lunch"   -> new YelpPlugin,
    "help"    -> new HelperPlugin
  )


  def find(name:String): Option[GenericPlugin] = {
    m.get(name)
  }

}
