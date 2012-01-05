/******************************************************************************
 * Copyright (c) 2012. SoftExcel Technologies Inc. (SoftExcel),               *
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
import java.net.{SocketException, Socket}
import java.io.DataOutputStream
import net.liftweb.http.{RemoveAListener, AddAListener}
import net.liftweb.common.Logger

/**
 *
 * User: Anupam Chandra
 * Date: 12/21/11
 * Time: 12:42 PM
 */
case class SocketBasedTokenObserver(socket: Socket) extends LiftActor with Logger {
  info("Adding a new Socket Connection. Max value of int is = " + Int.MaxValue)
  TokenRefresher ! AddAListener(this)
  val out = new DataOutputStream(socket.getOutputStream())

  def messageHandler = {
    case x: Int => {
      out.writeInt(x)
    }
  }

  override def exceptionHandler = {
    case e: SocketException => TokenRefresher ! RemoveAListener(this)
    case _ => {
      if (socket.isConnected) {
        if (!socket.isOutputShutdown) out.close()
        socket.close()
      }
      TokenRefresher ! RemoveAListener(this)
      super.exceptionHandler
    }
  }
}