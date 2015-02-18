package com.mblund.todo.domain

import com.mblund.todo.technology.Command

case object Undo extends Command
case object Redo extends Command

sealed trait TodoCommand extends Command
case object AddTodoList extends TodoCommand
case class RemoveTodoList(listId: Long) extends TodoCommand
case class AddTodo(listId: Long, text: String) extends TodoCommand
case class RemoveTodo(id: Long) extends TodoCommand
case class ChangeTodoStatus(id: Long, newStatus: Boolean) extends TodoCommand
case class ChangeTodoText(id: Long, newText: String) extends TodoCommand

