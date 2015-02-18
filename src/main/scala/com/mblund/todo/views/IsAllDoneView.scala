package com.mblund.todo.views

import com.mblund.todo.domain.{TodoStatusChanged, TodoRemoved, TodoAdded, TodoEvent}
import com.mblund.todo.technology.{Subscriber, Topic}
import com.mblund.todo.technology.technology._

import scalatags.JsDom.all._

class IsAllDoneView(listId: Long, eventsTopic: Topic[TodoEvent], execute: Executor)
  extends View with Subscriber[TodoEvent] {

  eventsTopic.subscribe(this)
  val el = label().render
  var todos = collection.mutable.Map[Long, Boolean]()
  updateText()

  def receive = {

    case TodoAdded(todoId, `listId`, text) => {
      //TODO här hade jag en bugg case TodoAdded(`listId`, todoId, text)
      println(s"foo $listId , $todoId : $text")
      todos += (todoId -> false)
      updateText()
    }
    case TodoRemoved(todoId) => {
      todos.remove(todoId)
      updateText()
    }
    case TodoStatusChanged(todoId, status) => {
      println("------" + todoId + " values:" + todos.values.mkString(","))
      todos.get(todoId) match {
        case Some(x) => {
          todos.update(todoId, status)
          println(status)
        }
        case None =>
      }
      updateText()
    }
  }

  def updateText() = {
    val nrFinishedTodos = todos.values.count(_ == true)
    el.textContent = s"$nrFinishedTodos of  ${todos.size} to-dos are done."
  }
}