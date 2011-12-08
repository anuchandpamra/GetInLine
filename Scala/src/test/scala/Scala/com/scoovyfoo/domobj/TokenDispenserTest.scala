package Scala.com.scoovyfoo.domobj

import org.junit.Test;
import org.junit.Assert.assertEquals
import com.scoovyfoo.domobj.TokenDispenser
import actors.Actor._
;

/**
 *
 * User: Anupam Chandra
 * Date: 11/29/11
 * Time: 3:49 PM
 */

class TokenDispenserTest {
  @Test
  def testNextNumber() {
    val a = new TokenDispenser(1304)
    assertEquals(1304, a.nextNumber)
    assertEquals(1305, a.nextNumber)
    assertEquals(1306, a.nextNumber)

    for (i <- 1307 to 101307)
      assertEquals(i, a.nextNumber)
  }

  @Test
  def testReset() {
    val a = new TokenDispenser(1304)
    assertEquals(1, a.reset(1).nextNumber)
    assertEquals(2, a.nextNumber)
    assertEquals(3, a.nextNumber)
  }

  @Test
  def testMultiThreaded() {
    val a = new TokenDispenser(100)
    val actorOne = actor {
      for (i <- 1 to 100)
        println("ActorOne => " + a.nextNumber)
    }
    val actorTwo = actor {
      for (i <- 1 to 100)
        println("ActorTwo => " + a.nextNumber)
    }
  }

  @Test
  def testPeek() {
    val a = new TokenDispenser(1304)
    assertEquals(1304, a.peek)
    assertEquals(1304, a.peek)
    assertEquals(1304, a.peek)
    a.nextNumber
    a.nextNumber
    assertEquals(1306, a.peek)
    val actorTwo = actor {
      a.nextNumber
      a.nextNumber
      a.nextNumber
    }
    // Let the other thread finish so sleep for a while.
    Thread.sleep(100)
    assertEquals(1309, a.peek)
  }

}