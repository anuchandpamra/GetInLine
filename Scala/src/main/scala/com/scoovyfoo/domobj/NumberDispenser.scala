package com.scoovyfoo.domobj

/**
 *
 * User: Anupam Chandra
 * Date: 11/29/11
 * Time: 2:12 PM
 */

class NumberDispenser(startingNumber: Int) {
  def numberStream(num: Int): Stream[Int] =
    num #:: numberStream(num + 1)

  private var nextNumberStream = numberStream(startingNumber)

  def nextNumber: Int = {
    val nextNum = nextNumberStream.head;
    nextNumberStream = nextNumberStream.tail
    nextNum
  }
}