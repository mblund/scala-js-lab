package com.mblund.todo.domain

sealed trait TodoEvent
case class TodoListAdded(id: Long) extends TodoEvent
case class TodoListRemoved(id: Long) extends TodoEvent
case class TodoAdded(id: Long, listId: Long, text: String) extends TodoEvent
case class TodoRemoved(id: Long) extends TodoEvent
case class TodoStatusChanged(todoId: Long, newStatus: Boolean) extends TodoEvent
case class TodoTextChanged(todoId: Long, newText: String) extends TodoEvent
case object Reset extends TodoEvent

object TodoEvent {

  def pickle(evt: TodoEvent): String = upickle.write(evt)
  def unpickle(text: String): TodoEvent = upickle.read[TodoEvent](text)

}