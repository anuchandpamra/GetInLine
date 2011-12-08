package Lift.snippet

import Lift.model.AQueue
import net.liftweb._
import http._
import util.Helpers._
import js._
import JsCmds._



/**
 * 
 * User: Anupam Chandra
 * Date: 12/8/11
 * Time: 9:40 AM
 */

object TokenDispenser {
    def render = {

    // our process method returns a
    // JsCmd which will be sent back to the browser
    // as part of the response
    def process(): JsCmd= {
      // sleep for 400 millis to allow the user to
      // see the spinning icon
      Thread.sleep(400)
      S.notice("myToken", "Your token Number is: "+AQueue.tokenDispenser.nextNumber.toString)
      Noop
    }

    // binding looks normal
    "#hidden" #> SHtml.hidden(process)
  }
}