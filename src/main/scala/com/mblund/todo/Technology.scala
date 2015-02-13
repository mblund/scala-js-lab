package com.mblund.todo.technology

package object technology {
  type Executor = {
    def run(command: Command): Unit
  }
}

trait Command

trait Subscriber[T] {

  def receive: PartialFunction[T, Unit]
}

class IdGenerator {
  private var id = -1L

  def getNextId(): Long = {
    id = id + 1L
    id
  }
}

class Topic[T] {
  private var subscribers = Set.empty[Subscriber[T]]

  def subscribe(subscriber: Subscriber[T]) = subscribers = subscribers + subscriber

  def unsubscribe(subscriber: Subscriber[T]) = subscribers = subscribers.filterNot(_ == subscriber)

  def unsubscribeAll() = subscribers = Set.empty[Subscriber[T]]

  def distribute(t: T) = {
    val a = subscribers.filter(_.receive.isDefinedAt(t))
    println(t + "to :" + a)
    a.foreach(x => x.receive(t))
  }
}

import org.scalajs.dom.Element
trait View {
  def el: Element

  def removeItself() = {
    if (el.parentNode != null) {
      el.parentNode.removeChild(el)
    }
  }
}
