package Lift.snippet

import _root_.net.liftweb.util._
import Helpers._

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