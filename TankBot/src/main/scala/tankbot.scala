package com.wordnik.irc.tankbot

import scala.util._
import scala.math._

object GravitationalConstant {
  val g = 9.8
}

abstract class Renderer {
  def render
}

object Printer extends Renderer {
  def render(l:Landscape, tl:List[TankChar], tj:Option[Trajectory]) = {

  }
}

class TankChar

class Land extends TankChar {
  override def toString = "#"
}

class Air extends TankChar {
  override def toString = " "
}

class Explosion extends TankChar {
  override def toString = "*"
}

class Tank(x:Int=0, y:Int=0, id:Int=0) extends TankChar {
  var health = 100
  override def toString = "T"
  
  def fire(angle: Double, velocity: Double): Trajectory = {
    new Trajectory(angle, velocity, x, y)
  }
}

class Column(height:Int=0) {
  var contents:Array[TankChar] = Array.fill(height)(new Air)

  def fill(height:Int) {
    for ( n <- 0 until height ) {
      this.contents(n) = new Land
    }
  }

  def isLand(y:Int): Boolean = {
    return ( y > 0 && y < this.contents.length)
  }
  def destroy(y:Int) {
    this.contents(this.contents.length - 1) = new Explosion
  }
  def getHeight: Int = {
    this.contents.collect( { case l: Land => true } ).length
  }
  def getChar(y:Int): TankChar = {
    if ( y >= 0 && y < this.contents.length ) {
      return this.contents(y)
    } else {
      return new Air
    }
  }
  def putTank(tank:Tank=new Tank) {
    if ( this.contents.length == 0 ) { this.contents = Array(tank) }
    else { this.contents(this.getHeight) = tank } }

  def putWhiff(y:Int) {
    if ( y > this.contents.length ) { return None }
    this.contents(y) = new Explosion
  }

}

class Landscape(width:Int=75, maxHeight:Int=25, bumpiness:Int=2, tanks:Int=1) {
  // Constructor goes here...
  val r = new Random
  var currentHeight = r.nextInt(maxHeight)
  if ( currentHeight < 1 ) { currentHeight = 1 }

  var columns = Array.fill(width)(new Column)
  for ( i <- 0 until width ) {
    val col = new Column(maxHeight)
    col.fill(currentHeight)
    columns(i) = col
    
    var delta = r.nextInt(bumpiness)                         // to add some bumpiness to the Landscape
    if ( r.nextBoolean() ) { delta = delta * -1 }            // 50% chance of addition, 50% chance of subtraction
    
    var nextHeight = abs(currentHeight + delta)
    if ( nextHeight > maxHeight ) { nextHeight = maxHeight }
    if ( nextHeight < 1 ) { nextHeight = 1 }
    currentHeight = nextHeight
  }

  def placeTanks(numTanks:Int=1): List[Tank] = {
    val tankList = for {
      t <- 0 until numTanks
      val buffer = 5
      val widthPerTank = floor((width - (buffer * tanks)) / tanks).toInt
      val tankPos = (widthPerTank * t) + r.nextInt(widthPerTank) + (buffer * t)
      val tank = new Tank(tankPos, columns(tankPos).getHeight, id=t)
    } yield tank
    return tankList.toList
  }




  override def toString: String = {
    var s = "\n\n"
    for ( i <- 1 to maxHeight ) {
      for ( j <- 0 until width ) {
	val h = maxHeight - i
	s += columns(j).getChar(h)
      }
      s += "\n"
    }
	
    return s
  }
}

class Trajectory(angle:Double, velocity:Double, initialX:Int=0, initialY:Int=0) {
  val g = GravitationalConstant.g
  val theta = toRadians(angle)
  
  def getHeight(x:Int): Double = {
    /* http://en.wikipedia.org/wiki/Trajectory */
    return ( x * tan(theta) - ( (g * pow(x, 2)) / ( 2 * pow(velocity * cos(theta), 2) ))) + initialY
  }
  
  def getPos(time:Double): Tuple2[Double, Double] = {
    val horizVel  = velocity * cos(theta)
    val vertVel   = velocity * sin(theta)
    val xPosition = horizVel * time
    val yPosition = ( vertVel * time ) - ( .5 * g * pow(time, 2) )
    return (xPosition / 10, yPosition/ 10 + initialY) // we divide by 10 for the numbers to make sense
						      // in the Tank context
  }
}

object InputGrabber {
  def getInput(display:String): String = {
    return Console.readLine(display + ": ")
  }
}

object Game {
  def main(args: Array[String]=Array("2")) {
    println(args.toString)
    this.play(args(0).toInt)
  
  }
  def play(numTanks:Int=0) {
    val l = new Landscape
    val t = l.placeTanks(numTanks)
    // while ( true ) {
    //   val a = InputGrabber.getInput("Enter angle").toInt
    //   val v = InputGrabber.getInput("Enter velocity").toInt
    //   val t = new Trajectory(a,v,0,l.getHeightAt(0))
      
    // }
  }
}
