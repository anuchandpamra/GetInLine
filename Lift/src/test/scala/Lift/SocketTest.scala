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

package Lift

import java.net.{Socket, InetAddress}
import java.io.{IOException, DataInputStream}

/**
 *
 * User: Anupam Chandra
 * Date: 12/21/11
 * Time: 5:45 PM
 */

object SocketTest {
  def main(args: Array[String]) {
    try {
      //val ia = InetAddress.getByName("ec2-107-22-70-130.compute-1.amazonaws.com")
      val ia = InetAddress.getByName("localhost")
      val socket = new Socket(ia, 7201)

      val in = new DataInputStream(socket.getInputStream())

      var i = 20
      while (i > 0) {
        val x = in.readInt()
        println("x = " + x)
        i -= 1
      }
      in.close()
      socket.close()
    }
    catch {
      case e: IOException =>
        e.printStackTrace()
    }
  }
}