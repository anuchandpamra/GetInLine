package Lift.comet


import Lift.model.AQueue
import net.liftweb.http.{CometListener, CometActor}

/**
 *
 * User: Anupam Chandra
 * Date: 12/7/11
 * Time: 3:14 PM
 */

class  TokenObserver extends CometActor with CometListener {
  private var nextTokenNumberToBeDisplayed = AQueue.tokenDispenser.peek

  // Register with TokenRefresher
  def registerWith = TokenRefresher

  override def lowPriority = {
    case i: Int => nextTokenNumberToBeDisplayed = i;reRender()
  }

  def render = {
    "#token *" #> nextTokenNumberToBeDisplayed
  }
}