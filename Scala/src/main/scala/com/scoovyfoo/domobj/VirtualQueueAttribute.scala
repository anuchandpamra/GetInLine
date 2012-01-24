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

package com.scoovyfoo.domobj

/**
 *
 * User: Anupam Chandra
 * Date: 1/23/12
 * Time: 2:43 PM
 */

trait VirtualQueueAttribute {
  def display: String
}

case class VirtualQueueOwner(id: String, name: String) extends VirtualQueueAttribute {
  override def display = id + " " + name
}

case class VirtualQueueLocation(latitude: Double, longitude: Double) extends VirtualQueueAttribute {
  override def display = "lat:" + latitude + ", long:" + longitude
}

case class VirtualQueueAddress(number: String, street1: String, street2: String, street3: String,
                               postalCode: String, city_town_locality: String, state_province: String, country: String)
extends VirtualQueueAttribute{
  override def display = number + " " + street1 + "\n" + street2 + "\n" + street3 +"\n" + city_town_locality + ", " + postalCode + "\n" + state_province + "\n" + country
}

case class VirtualQueuePhoneNumber(phone: String) extends VirtualQueueAttribute{
  override def display = phone
}