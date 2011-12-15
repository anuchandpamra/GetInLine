package Lift.snippet

import Lift.model.AQueue
import net.liftweb._
import http._
import util.Helpers._
import js._
import JsCmds._
import Lift.comet.TokenRefresher


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
    def process() : JsCmd= {
      S.notice("myToken", "Your token Number is: "+AQueue.tokenDispenser.dispense.toString)
      TokenRefresher ! "R"
      Noop
    }

    // binding looks normal
    "#hidden" #> SHtml.hidden(process)
  }
}