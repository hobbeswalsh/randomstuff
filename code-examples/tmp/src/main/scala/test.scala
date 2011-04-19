/*
 * We'll comment stuff out as we no longer need it.

val stuffs = List("one", 2, 't', 4.0)
for ( item <- stuffs ) {
  item match {
    case i: Int => println(i + " is an integer.")
    case d: Double => println(d + " is a double.")
    case s: String => println(s + " is a string.")
    case other: Any => println(other + " is something else...")
  }
}

import scala.util.Random

val randomGenerator = new Random()

for ( roll <- 1 to 9999 ) {
  randomGenerator.nextInt(1000) match {
    case 7 => println("Lucky 7")
    case 77 => println("Double lucky double 7")
    case 777 => println("AMAZING 777!!!")
    case other => None
  }
}


val l1 = List(1,2,3,4,5)
val l2 = List(9,8,7,6,5)
val l3 = List()

def processList(l: List[Any]): Unit = l match {
  case head :: tail =>
    println(format("Just pulled off %s", head))
    processList(tail)
  case Nil =>
    println("All done!")
}

for ( l <- List(l1, l2, l3) ) {
  println("List " + l)
  processList(l)
}

val t1 = (1, 2, 3)
val t2 = (5, 6, 7)
val t3 = (8, 9)
val t4 = "ten"

for ( t <- List(t1, t2, t3, t4) ) {
  t match {
    case (one, two)        => println("This tuple has two items")
    case (one, two, three) => println("This tuple has three items")
    case other             => println("Eh, what? " + other)
  }
}

package com.robin.test

case class Wordnik(name: String, job: String, age: Int)

val Robin  = new Wordnik("Robin", "SA", 29)
val Robert = new Wordnik("Robert", "Programmer", 30)
val Tony   = new Wordnik("Tony", "Total geek", 38)
val Erin   = new Wordnik("Erin", "Lexicographer", 38)
val Mark   = new Wordnik("Mark", "Ninja Programmer", 38)
val John   = new Wordnik("John", "Web Programmer", 38)
val Zeke   = new Wordnik("Zeke", "Design Hacker", 38)
val Kumanan = new Wordnik("Kumanan", "Mad Programmer geek", 38)

val NinjaRe = """[Pp]rogrammer""".r 
val NinjaRe = New Regex """[Pp]rogrammer"""
com.ro
for ( person <- List(Robin, Robert, Tony, Erin, Mark, John, Zeke, Kumanan) ) {
  person match {
    case Wordnik("Robin", _, _) => println("We found Robin.")
    case Wordnik(_, "Lexicographer", _) => println("We found a lexicographer")
     
    case _                           => println("We don't cover this case yet.")
  }
}

 





class Game {
  import ArgotConverters._
  // Do some Argot initialization for command-line options
  val p      = new ArgotParser("RandomWordGetter", preUsage=Some("Version 1.0"))
  val apiKey = p.option[String](List("k", "key"), "key", "API key (required)")
  val hasDef = p.flag[Boolean](List("d", "hasDef"), "Has a dictionary definition.")
  
  // jsonObj's "extract" method needs an implicit "formats" argument in our namespace
  implicit val formats = DefaultFormats
  def main(args: Array[String]): Any = {
    p.parse(args)

  }
  def play(words: List[WordnikWord]): Boolean = {
    if (words.length != 2)
      throw new Exception("Uh oh!")
    var firstGuess = words(0)
    var secondGuess = words(1)
    while (firstGuess != secondGuess) {
      secondGuess = WordnikWord(InputGrabber.getInput)
      println(firstGuess + " :: " + secondGuess)
    } 
    return true
  }
}

// GET /word.{format}/{word}/definition
// http://api.wordnik.com/v4/words.json/search?allowRegex=true&api_key=YOUR_API_KEY&minDictionaryCount=5&query=cata
// http://api.wordnik.com/v4/word.json/zook/definitions?api_key=YOUR_API_KEY

object RandomWordGetter {

  def getWord(apiKey: String): WordnikWord = {
    val key = apiKey
    

    var url     = "http://api.wordnik.com/v4/words.json/randomWord?api_key=" + key
    val jsonString = Source.fromURL(url).mkString

    val wordnikGuess = parse(jsonString).extract[WordnikWord]
    wordnikGuess
  }
 
  def main(args: Array[String]): Any =  {
   
    val userGuess    = WordnikWord(InputGrabber.getInput)
    val wordnikGuess = this.getWord
    
    if ( wordnikGuess == userGuess) {
      println("Zomg. you must read minds.")
      System.exit(0)
    } else {
      val g = new Game()
      g.play(List(wordnikGuess, userGuess))
    }
  }
}
*/

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
    println(jsonString)
    val definitions = parse(jsonString).extract[WordnikDefinitions]
    if (definitions.defs.isEmpty) { return false }
    else { return true }
  }
}

object WordGuesser {
  val url = "http://api.wordnik.com/v4/words.json/search?allowRegex=true"

  implicit val formats = DefaultFormats  // For casting JSON to case classes

  def guess(chars: String): Char = {
    val jsonString = Source.fromURL(url + "&api_key=" + Game.apiKey + "&query=" + chars + ".*").mkString
    val wordFreqs = parse( "{ \"list\":" + jsonString + "}" ).extract[WordnikFrequencyList]
    val nonZero = for { wf <- wordFreqs.list if wf.count != 0 } yield wf
    if ( nonZero.isEmpty ) {
      println("No definitions for that, cheater!")
      System.exit(1)
    }
    val choice = util.Random.shuffle(nonZero).take(1)(0)
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
  val apiKey = "d92d8109432f0ead8000707303d0c6849e23be119a18df853"
  val url = "http://api.wordnik.com/v4/word.json"

  var word = ""
  implicit val formats = DefaultFormats  // For casting JSON to case classes
  
  def main(args: Array[String]): Any = {
    this.play(InputGrabber.getInput)
  }
  def play(guess: String): Any = {
    this.word += guess
    
    // if ( this.word.length > 3 && Validator.validate(WordnikWord(this.word)) ) {
    //   println( this.word + " is a word! You lose, loser!" )
    //   val jsonString = Source.fromURL(url + "/" + this.word + "/definitions?api_key=" + apiKey).mkString
    //   val definitions = parse("{ \"defs\":" + jsonString + "}").extract[WordnikDefinitions]

    //   println(definitions.defs(0).word + ": " + definitions.defs(0).text)

    //   System.exit(0)
    // }

    this.word += WordGuesser.guess(this.word)
    // if ( this.word.length > 3 && Validator.validate(WordnikWord(this.word)) ) {
    //   println("The stupid computer lost!")
    //   val jsonString = Source.fromURL(url + "/" + this.word + "/definitions?api_key=" + apiKey).mkString
    //   val definitions = parse("{ \"defs\":" + jsonString + "}").extract[WordnikDefinitions]

    //   println(definitions.defs(0).word + ": " + definitions.defs(0).text)
    //   System.exit(0)	
    // }
    println( this.word + " still isn't a word. Keep trying..." )
    val humanGuess = InputGrabber.getInput
    if ( humanGuess == "surrender" ) { WordGuesser.reveal(this.word) }
    else { this.play(humanGuess) }
  }
}


// Get /word.{format}/{word}/definition
// http://api.wordnik.com/v4/words.json/search?allowRegex=true&api_key=YOUR_API_KEY&minDictionaryCount=5&query=cata
// http://api.wordnik.com/v4/word.json/zook/definitions?api_key=YOUR_API_KEY
