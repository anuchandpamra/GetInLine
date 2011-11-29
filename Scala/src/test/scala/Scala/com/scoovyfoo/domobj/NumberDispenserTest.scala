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
   def testNumberDispenser () {
     val a = new NumberDispenser(1304)
     assertEquals(1304, a.nextNumber)
     assertEquals(1305, a.nextNumber)
     assertEquals(1306, a.nextNumber)

     for (i <- 1307 to 101307)
       assertEquals(i,a.nextNumber)
   }
}