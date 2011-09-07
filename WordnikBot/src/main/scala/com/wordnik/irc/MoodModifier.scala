package com.wordnik.irc

/*
 * -rwalsh (via IntelliJ) 
 */

object MoodModifier {
  val moodWords = Map[String, Int] (
    "happy"     -> 2,
    "glad"      -> 2,
    "amazing"   -> 2,
    "cool"      -> 1,
    "fun"       -> 1,


    "sad"      -> -2,
    "bored"    -> -1,
    "boring"   -> -1,
    "stupid"   -> -1,
    "dumb"     -> -1,


    "crappy"   -> -1

  )

  def modify(word: String): Int = {
    this.moodWords.getOrElse(word, 0)
  }
}