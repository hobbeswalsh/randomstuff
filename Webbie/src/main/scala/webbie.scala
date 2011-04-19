import scala.actors._
import scala.actors.Actor._


/*
object Upper {
  def main(args: Array[String]) = {
    args.map(_.toUpperCase()).foreach(printf("%s ",_))
    println("")
  }
}

object Lower {
  def main(args: Array[String]) = {
    args.map(_.toLowerCase()).foreach(printf("%s ",_))
    println("")
  }
}
*/

package shapes {

  class Point(val x: Double, val y: Double) {
    override def toString() = "Point(" + x + "," + y + ")"
  }

  abstract class Shape {
    def draw(): Unit
  }

  class Circle(val center: Point, val radius: Double) extends Shape {
    override def draw() = println("Circle.draw: " + this)
    override def toString() = "Circle(" + center + "," + radius + ")"
  }

  class Rectangle(val lowerLeft: Point, val height: Double, val width: Double) extends Shape {
    override def draw() = println("Rectangle.draw: " + this)
    override def toString() = "Rectangle(" + lowerLeft + "," + height + "," + width + ")"
  }

/*
 * We're not allowed to extend Rectangle unless we use the same parameters in the
 * constructor?
 */

  class Square(lowerLeft: Point, val side: Double) extends Rectangle(lowerLeft, side, side) {
    override def draw() = println("Square.draw: " + this)
    override def toString() = "Rectangle(" + lowerLeft + "," + height + "," + width + ")"
  }

  class Triangle(val point1: Point, val point2: Point, val point3: Point) extends Shape {
    override def draw() = println("Triangle.draw: " + this)
    override def toString() = "Triangle(" + point1 + "," + point2 + "," + point3 + ")"
  }

  object ShapeDrawingActor extends Actor {
    def act() {
      loop {
	receive {
	  case s: Shape => s.draw()
	  case "exit"   => println("exiting..."); exit
	  case x: Any   => println("Error: Unknown message sent to " + this + ":" + x )
	}
      }
    }
  }
}
