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