package com.wordnik.irc.tankbot

import scala.util.Random
import scala.math._

object GravitationalConstant {
  val g = 9.8
}

abstract class Renderer {
  def render(input: String): Any
  def render(input: List[String]): Any
}

class Printer extends Renderer {
  def render(input: String): Any = {
    println(input)
  }
  def render(input: List[String]): Any = {
    for { line <- input } println(line)
  }
}

// This is a comment...

class Landscape(width:Int=75, maxHeight:Int=25, bumpiness:Int=2, ren:Renderer=new Printer) {

  val r = new Random
  val initialHeight = r.nextInt(maxHeight)
  var currentHeight = initialHeight
  var columns = Array.fill(width, maxHeight)(0)
  for ( i <- 0 until width ) {
    var delta    = r.nextInt(bumpiness)
    val negative = r.nextBoolean()
    if ( negative ) {
      delta = delta * -1
    }
    var nextHeight = abs(currentHeight + delta)
    if ( nextHeight > maxHeight ) { nextHeight = maxHeight }
    for ( n <- 0 until currentHeight ) {
      columns(i)(n) = 1
    }
    currentHeight = nextHeight
  }

  def isLand(point:Tuple2[Int, Int]): Boolean = {
    val x = point._1
    val y = point._2
    return columns(x)(y) == 1
  }
  def destroy(point:Tuple2[Int, Int]): Any = {
    if ( this.isLand(point) ) {
      val x = point._1
      val y = point._2
      columns(x)(y) = -1
    }
  }
  def getHeightAt(x:Int): Int = {
    return columns(x).filter( i => i == 1).sum
  }
  def render: Any = {
    this.ren.render(this.toString)
  }

  override def toString: String = {
    var s = "\n\n\n"
    for ( i <- 1 to maxHeight ) {
      for ( j <- 1 to width ) {
	val w = width - j
	val h = maxHeight - i
	if ( columns(w)(h) == 1 ) { s += "#" }
	if ( columns(w)(h) == 0 ) { s += " " }
	if ( columns(w)(h) == -1 ) { s += "*" }

      }
      s += "\n"
    }

	
    return s
  }
}

class Trajectory(velocity:Int, angle:Int, initialX:Int=0, initialY:Int=0) {
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
    return (xPosition / 10, yPosition/ 10 + initialY) // we divide by 10 for the numbers to make sense in the Tank context
  }
}

class Tank {

}

object Game {
  def main(args: Array[String]) {
    val l = new Landscape
    val t = new Trajectory(args(0).toInt, args(1).toInt, initialX=0, initialY=10)
    l.render
    println(l.getHeightAt(1))
    println(l.getHeightAt(2))
    l.destroy(2, l.getHeightAt(2) - 1)
    println(l.getHeightAt(2))
    l.render

     

    
  }
}
