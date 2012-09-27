package recfun
import common._

object Main {
  def main(args: Array[String]) {
    println("Pascal's Triangle")
    for (row <- 0 to 10) {
      for (col <- 0 to row)
        print(pascal(col, row) + " ")
      println()
      
      
    }
    println("Balance function")
    
    println(balance("this is simple".toList))
    println(balance("this is (too)".toList))
    println(balance("uh oh (we're not gonna close".toList))
    println(balance("())(".toList))
    println(balance("(this(sentence(has(lots(of(nested(parens)))))))".toList))
    
    println(countChange(100, List(5, 100, 50, 25, 10, 1)))
  }

  /**
   * Exercise 1
   */
  // faster with memoization
  private var memo = Map[String, Int]()
  def pascal(c: Int, r: Int): Int = {
    // I know this is a dumb way to store the keys.
    val key = c.toString + " " + r.toString
    val stored = memo.get(key)
    // If we've already computed the value we're being asked for,
    // return it immediately
    if ( stored != None ) {
      return stored.get
    }
    
    // zero values (and smaller!) return 1
    if (c <= 0 || r <= 0) {
    	val storing = c.toString() + " " + r.toString()
    	memo = memo + (storing -> 1)
    	return 1
    // everything on the first row == 1
	} else if (r == 1) {
		val storing = c.toString() + " " + r.toString()
    	memo = memo + (storing -> 1)
    	return 1
    // if we're further out than the number of rows we've gone down,
    // return 1.
	} else  if (c >= r) {
		val storing = c.toString() + " " + r.toString()
    	memo = memo + (storing -> 1)
    	return 1
	// here we can compute the value.
	} else {
		val computed_value = pascal((c - 1), (r-1)) + pascal((c), (r - 1))
		val storing = c.toString() + " " + r.toString()
		memo = memo + (storing -> computed_value)
		return computed_value
	}
    
  
  }

  /**
   * Exercise 2
   */
  def balance(chars: List[Char]): Boolean = {
    var parensDeep = 0
    
    def parenStack(currentChar: Char, remainingChars: List[Char], openParens: Int): Int = {
    

      if ( currentChar == '(' ) {
        parensDeep = openParens + 1
      } else if ( currentChar == ')' ) {
        parensDeep = openParens - 1
      } 
      if ( remainingChars.isEmpty ) {
        return parensDeep
      }
      if ( parensDeep < 0 ) {
        return -1
      }
      parenStack(remainingChars.head, remainingChars.tail, parensDeep)
    }
    val remainingParens = parenStack(chars.head, chars.tail, parensDeep)
    return ( remainingParens == 0 )
  }

  /**
   * Exercise 3
   */
  def countChange(money: Int, coins: List[Int]): Int = {
	  if ( money == 0 ) {
	    return 0
	  }
	  if ( coins.isEmpty ) {
	    return 0
	  }
	  if ( coins.head > money ) {
	    return countChange(money, coins.tail)
	  }
	  if ( coins.head == money ) {
	    return 1 + countChange(money, coins.tail)
	  }
	  return countChange(money - coins.head, coins) + countChange(money, coins.tail)
    
  }
}
