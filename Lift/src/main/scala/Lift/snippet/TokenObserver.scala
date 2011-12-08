package Lift.snippet

import _root_.scala.xml.{NodeSeq, Text}
import _root_.net.liftweb.util._
import _root_.net.liftweb.common._
import _root_.java.util.Date
import Lift.lib._
import Helpers._

import com.scoovyfoo.domobj.TokenDispenser
import Lift.model.AQueue

/**
 * 
 * User: Anupam Chandra
 * Date: 12/7/11
 * Time: 3:14 PM
 */

object TokenObserver {
    def render = {
      "#token *" #> AQueue.tokenDispenser.peek
    }
}