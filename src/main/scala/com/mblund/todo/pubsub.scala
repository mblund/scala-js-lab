package com.mblund.todo.technology

import java.util.Date

import scala.util.Random

package object technology {
  type Executor = Command => Unit
}

trait Command

trait Subscriber[T] {

  def receive: PartialFunction[T, Unit]

}

class IdGenerator {

  def getNextId(): Long = Random.nextLong()

}

class Topic[T] {

  private var subscribers = Set.empty[Subscriber[T]]

  def subscribe(subscriber: Subscriber[T]): Unit = {
    subscribers += subscriber
  }

  def unsubscribe(subscriber: Subscriber[T]): Unit = {
    subscribers = subscribers.filterNot(_ == subscriber)
  }

  def unsubscribeAll(): Unit = {
    subscribers = Set.empty[Subscriber[T]]
  }

  def distribute(t: T): Unit = {
    subscribers.foreach(x => x.receive.lift(t))
  }

}