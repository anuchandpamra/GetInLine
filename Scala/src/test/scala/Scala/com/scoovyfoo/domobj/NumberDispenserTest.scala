package Scala.com.scoovyfoo.domobj

import org.junit.Test;
import org.junit.Assert.assertEquals
import com.scoovyfoo.domobj.NumberDispenser
;

/**
 * 
 * User: Anupam Chandra
 * Date: 11/29/11
 * Time: 3:49 PM
 */

class NumberDispenserTest{
   @Test
   def testNextNumber () {
     val a = new NumberDispenser(1304)
     assertEquals(1304, a.nextNumber)
     assertEquals(1305, a.nextNumber)
     assertEquals(1306, a.nextNumber)

     for (i <- 1307 to 101307)
       assertEquals(i,a.nextNumber)
   }

  @Test
  def testReset(){
    val a = new NumberDispenser(1304)
    assertEquals(1, a.reset(1).nextNumber)
    assertEquals(2, a.nextNumber)
    assertEquals(3, a.nextNumber)

  }


}