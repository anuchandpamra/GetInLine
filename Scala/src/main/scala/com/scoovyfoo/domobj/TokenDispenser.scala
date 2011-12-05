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
    private def numberStream(num: Int): Stream[Int] =
      num #:: numberStream(num + 1)

    private var nextNumberStream = numberStream(startingNumber)

    def act() {
      loop {
        react {
          case ("DISPENSE", actor : Actor) => {
            actor ! nextNumberStream.head
            nextNumberStream = nextNumberStream.tail
          }
          case ("RESET", resetTo: Int) => {
            nextNumberStream = numberStream(resetTo)
          }
        }
      }
    }
  }

  def reset(resetTo: Int) = {
      NumberDispenser ! ("RESET",resetTo)
      this
  }
  def nextNumber: Int = {
    val msg = ("DISPENSE",self)
    NumberDispenser ! msg
    self.receive {case nextNum:Int => nextNum}
  }


}