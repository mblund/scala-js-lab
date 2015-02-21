package com.mblund.todo

import com.mblund.todo.domain.TodoEvent
import com.mblund.todo.technology.Subscriber
import org.scalajs.dom

class LocalStorageSubscriber extends Subscriber[TodoEvent] {

  private var counter = 0

  override def receive: PartialFunction[TodoEvent, Unit] = {

    case x: TodoEvent =>
      dom.localStorage.setItem(counter.toString, TodoEvent.pickle(x))
      counter += 1

  }

  def load(): Seq[TodoEvent] = {
    val events = for {
      n <- 0 until dom.localStorage.length
    } yield TodoEvent.unpickle(dom.localStorage.getItem(n.toString))
    dom.localStorage.clear()
    events
  }

}