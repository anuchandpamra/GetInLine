package Lift.comet

import net.liftweb.actor.LiftActor
import net.liftweb.http.ListenerManager
import Lift.model.AQueue

/**
 *
 * User: Anupam Chandra
 * Date: 12/8/11
 * Time: 12:44 PM
 */

object TokenRefresher extends LiftActor with ListenerManager {

  /* When we update the listeners we send the next available token
   */
  def createUpdate = AQueue.tokenDispenser.peek

  override def lowPriority = {
    case "R" => updateListeners()
  }
}