package com.wordnik.irc.plugins

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers


class MonkeyPluginTest extends FlatSpec with ShouldMatchers {
  "Monkey Plugin" should "contain a list of sayings" in {
    val p = new MonkeyPlugin
    p.replies should not be ('empty)
  }
  
  // "Monkey Plugin" should "return a valid reply" in {
 
  // }
}
