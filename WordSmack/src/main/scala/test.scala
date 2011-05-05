package com.wordnik.test

import org.clapper.argot._
import scala.io.Source
import net.liftweb.json._
import net.liftweb.json.JsonParser._

case class WordnikWord(word: String)

case class WordnikDefinitions(defs: List[WordnikDefinition])

case class WordnikDefinition(
  word: String,
  text: String,
  score: Double,
  partOfSpeech: Option[Any]=Option("Noun"),
  sourceDictionary: String,
  sequence: String
)

case class WordnikFrequency(count: Int, wordstring: String)
case class WordnikFrequencyList(list: List[WordnikFrequency])

object InputGrabber {
  def getInput: String = {
    return Console.readLine("Enter a letter: ")
  }
}

object Validator {
  val url = "http://api.wordnik.com/v4/word.json/"
  implicit val formats = DefaultFormats  // For casting JSON to case classes

  def validate(input: WordnikWord): Boolean = {
    // Lift bug. ew!
    val jsonString = "{ \"defs\": " + Source.fromURL(url + input.word + "/definitions?api_key=" + Game.apiKey).mkString + "}"
    val definitions = parse(jsonString).extract[WordnikDefinitions]
    if (definitions.defs.isEmpty) { return false }
    else { return true }
  }
}

object WordGuesser {
  val url = "http://api.wordnik.com/v4/words.json/search?allowRegex=true&minDictionaryCount=5"

  implicit val formats = DefaultFormats  // For casting JSON to case classes

  def guess(chars: String): Char = {
    val jsonString = Source.fromURL(url + "&api_key=" + Game.apiKey + "&query=" + chars + ".*").mkString
    val wordFreqs = parse( "{ \"list\":" + jsonString + "}" ).extract[WordnikFrequencyList]
    // filter out words that are current (length + 1)
    val nonZero = for { wf <- wordFreqs.list if wf.count != 0 } yield wf
    if ( nonZero.isEmpty ) {
      println("No definitions for that, cheater!")
      System.exit(1)
    }
    val possibleChoices = nonZero.filter(
      wf => wf.wordstring.length > (chars.length + 1)
    )
    if ( possibleChoices.isEmpty ) { println("I lose! I suck!"); System.exit(0) }
    val choice = util.Random.shuffle(possibleChoices).take(1)(0)
    println( "The computer is choosing '" + chars + choice.wordstring.charAt(chars.length) + "'" )
    return choice.wordstring.charAt(chars.length)
  }
  def reveal(word: String): Any = {
    val jsonString = Source.fromURL(url + "&api_key=" + Game.apiKey + "&query=" + word + ".*").mkString
    val wordFreqs = parse( "{ \"list\":" + jsonString + "}" ).extract[WordnikFrequencyList]
    val nonZero = for { wf <- wordFreqs.list if wf.count != 0 } yield wf
    for { freq <- nonZero } println(freq.wordstring)
    System.exit(0)
  }
}

object Game {
  val alphabet = 'a' to 'z'
  val apiKey = "deadbeef"
  val url = "http://api.wordnik.com/v4/word.json"

  var word = ""
  implicit val formats = DefaultFormats  // For casting JSON to case classes
  
  def main(args: Array[String]): Any = {
    this.play(InputGrabber.getInput)
  }
  def play(guess: String): Any = {
    this.word += guess
    
    if ( this.word.length > 3 && Validator.validate(WordnikWord(this.word)) ) {
      println( this.word + " is a word! You lose, loser!" )
      val jsonString = Source.fromURL(url + "/" + this.word + "/definitions?api_key=" + apiKey).mkString
      val definitions = parse("{ \"defs\":" + jsonString + "}").extract[WordnikDefinitions]

      println(definitions.defs(0).word + ": " + definitions.defs(0).text)

      System.exit(0)
    }

    this.word += WordGuesser.guess(this.word)
    if ( this.word.length > 3 && Validator.validate(WordnikWord(this.word)) ) {
      println("The stupid computer lost!")
      val jsonString = Source.fromURL(url + "/" + this.word + "/definitions?api_key=" + apiKey).mkString
      val definitions = parse("{ \"defs\":" + jsonString + "}").extract[WordnikDefinitions]

      println(definitions.defs(0).word + ": " + definitions.defs(0).text)
      System.exit(0)	
    }
    println( this.word + " still isn't a word. Keep trying..." )
    val humanGuess = InputGrabber.getInput
    if ( humanGuess == "surrender" ) { WordGuesser.reveal(this.word) }
    else { this.play(humanGuess) }
  }
}
