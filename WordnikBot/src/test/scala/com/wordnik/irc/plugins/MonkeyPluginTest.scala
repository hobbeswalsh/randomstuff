import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers

import com.wordnik.irc.plugins._
import com.wordnik.irc._

class MonkeyPluginSpec extends FlatSpec with ShouldMatchers {
  "Monkey Plugin" should "contain a list of sayings" in {
    val p = new MonkeyPlugin
    p.replies should not be ('empty)
  }
  
  // "Monkey Plugin" should "return a valid reply" in {
 
  // }
}
