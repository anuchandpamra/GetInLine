package Scala.com.scoovyfoo.domobj

import org.scalatest.{BeforeAndAfter, FunSuite}
import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith
import actors.Actor._
import actors.Actor.self
import actors.Actor
import com.scoovyfoo.domobj.TokenDispenser


/**
 *
 * User: Anupam Chandra
 * Date: 11/29/11
 * Time: 3:49 PM
 */

@RunWith(classOf[JUnitRunner])
class TokenDispenserTest extends FunSuite with BeforeAndAfter {
  private val tokenDispenser = new TokenDispenser(100)

  before {
    tokenDispenser.reset(1304)
  }


  test("dispense") {
    assert(1304 == tokenDispenser.dispense.number)
    assert(1305 == tokenDispenser.dispense.number)
    assert(1306 == tokenDispenser.dispense.number)

    for (i <- 1307 to 101307)
      assert(i == tokenDispenser.dispense.number)
  }

  test("reset") {
    assert(1 == tokenDispenser.reset(1).dispense.number)
    assert(2 == tokenDispenser.dispense.number)
    assert(3 == tokenDispenser.dispense.number)
  }

  test("MultiThreaded") {
    import collection.mutable.Map

    val aMap: Map[tokenDispenser.Token, String] = Map()
    // Create the first thread and make it start by sending a message of type tuple that has the "START" string and
    // the requesting thread
    val actorOne = actor {
      react {
        case ("START", requester: Actor) => {
          //
          for (i <- 1 to 100) {
            requester ! (tokenDispenser.dispense, "ActorOne" )
          }
          requester ! "EXIT"
        }
      }
    }
    // Create the second thread and start it just like the first thread
    val actorTwo = actor {
      react {
        case ("START", requester: Actor) =>{
          for (i <- 1 to 100) {
            requester ! (tokenDispenser.dispense, "ActorTwo")
          }
          requester ! "EXIT"
        }
      }
    }

    actorOne ! ("START", self)
    actorTwo ! ("START", self)
    var numActorsRunning = 2

    do {
      receive {
        case "EXIT" => numActorsRunning -= 1
        case (t: tokenDispenser.Token, s: String) => {
          intercept[NoSuchElementException] {
            try{aMap(t)} catch {case ex:NoSuchElementException => {aMap += (t -> s); throw ex} }
          }
        }
      }
    } while (numActorsRunning > 0)

    assert(200 == aMap.size)

  }

  test("Peek") {
    assert(1304 == tokenDispenser.peek)
    assert(1304 == tokenDispenser.peek)
    assert(1304 == tokenDispenser.peek)
    assert(tokenDispenser.dispense != tokenDispenser.dispense)
    assert(1306 == tokenDispenser.peek)
    val actorTwo = actor {
      tokenDispenser.dispense
      tokenDispenser.dispense
      tokenDispenser.dispense

    }
    // Let the other thread finish so sleep for a while.
    Thread.sleep(100)
    assert(1309 == tokenDispenser.peek)
  }

}