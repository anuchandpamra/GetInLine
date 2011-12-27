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

package Lift.model

import com.scoovyfoo.domobj.TokenDispenser
import Lift.comet.TokenRefresher
import net.liftweb.json.JsonAST.JValue
import xml.Node
import net.liftweb.json.{Xml, Extraction}

/**
 *
 * User: Anupam Chandra
 * Date: 12/7/11
 * Time: 10:16 PM
 */

object AQueue {
  val tokenDispenser = new TokenDispenser(100)

  def dispenseAToken = {
    val token = tokenDispenser.dispense
    TokenRefresher ! "R"
    token
  }

  private implicit val formats = net.liftweb.json.DefaultFormats

  implicit def toJson(token: tokenDispenser.Token): JValue = Extraction.decompose(token)

  implicit def toXml(token: tokenDispenser.Token): Node = <token>
    {Xml.toXml(token)}
  </token>
}