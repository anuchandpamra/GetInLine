/******************************************************************************
 * Copyright (c) 2011. SoftExcel Technologies Inc. (SoftExcel),               *
 * Virginia. All Rights Reserved. Permission to use, copy, modify, and        *
 * distribute this software and its documentation for educational,            *
 * research, and not-for-profit purposes, without fee and without a           *
 * signed licensing agreement, is hereby granted, provided that the above     *
 * copyright notice, this paragraph, and the next two paragraphs appear in    *
 * all copies, modifications, and distributions. Contact SoftExcel            *
 * TechnologiesInc. (info@softexcel.com), for commercial licensing            *
 * opportunities.                                                             *
 *                                                                            *
 * IN NO EVENT SHALL SOFTEXCEL BE LIABLE TO ANY PARTY FOR DIRECT,             *
 * INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES, INCLUDING         *
 * LOST PROFITS, ARISING OUT OF THE USE OF THIS SOFTWARE AND ITS              *
 * DOCUMENTATION, EVEN IF SOFTEXCEL HAS BEEN ADVISED OF THE POSSIBILITY       *
 * OF SUCH DAMAGE.                                                            *
 *                                                                            *
 * SOFTEXCEL SPECIFICALLY DISCLAIMS ANY WARRANTIES, INCLUDING, BUT NOT        *
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR      *
 * A PARTICULAR PURPOSE. THE SOFTWARE AND ACCOMPANYING DOCUMENTATION, IF      *
 * ANY, PROVIDED HEREUNDER IS PROVIDED "AS IS". SOFTEXCEL HAS NO              *
 * OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, OR      *
 * MODIFICATIONS.                                                             *
 ******************************************************************************/

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