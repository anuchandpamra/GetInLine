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

package com.scoovyfoo.domobj

import actors.Actor
import actors.Actor.self

/**
 *
 * User: Anupam Chandra
 * Date: 11/29/11
 * Time: 2:12 PM
 */

class TokenDispenser(startingNumber: Int) {
  NumberDispenser.start()

  private object NumberDispenser extends Actor {
    private def numberStream(num: Int): Stream[Int] = num #:: numberStream(num + 1)

    private var nextNumberStream = numberStream(startingNumber)

    def act() {
      loop {
        react {
          case ("DISPENSE", actor: Actor) => {
            actor ! nextNumberStream.head
            nextNumberStream = nextNumberStream.tail
          }
          case ("RESET", resetTo: Int) => nextNumberStream = numberStream(resetTo)
          case ("PEEK", actor: Actor) => actor ! nextNumberStream.head
        }
      }
    }
  }

  case class Token private[TokenDispenser](number: Int) {
    private[TokenDispenser] def copy: Token = throw new Error
  }

  def reset(resetTo: Int) = {
    NumberDispenser !("RESET", resetTo)
    this
  }

  def dispense: Token = {
    NumberDispenser !("DISPENSE", self)
    self.receive {
      case nextNum: Int => Token(nextNum)
    }
  }

  // Just peeks at the next number without getting it
  def peek: Int = {
    NumberDispenser !("PEEK", self)
    self.receive {
      case nextNum: Int => nextNum
    }
  }

}