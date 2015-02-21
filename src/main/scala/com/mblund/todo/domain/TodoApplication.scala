package com.mblund.todo.domain

import com.mblund.todo.technology.{Command, IdGenerator, Topic}


class TodoApplication(eventsTopic: Topic[TodoEvent]) {

  private val undoStack = new collection.mutable.ArrayStack[TodoEvent]()
  private val redoStack = new collection.mutable.ArrayStack[TodoEvent]()

  val idGenerator = new IdGenerator

  def generateEvent(command: TodoCommand): TodoEvent = command match {
    case AddTodoList => TodoListAdded(idGenerator.getNextId())
    case RemoveTodoList(listId: Long) => TodoListRemoved(listId)
    case AddTodo(listId: Long, text: String) => TodoAdded(idGenerator.getNextId(), listId, text)
    case RemoveTodo(id: Long) => TodoRemoved(id)
    case ChangeTodoStatus(id: Long, newStatus: Boolean) => TodoStatusChanged(id, newStatus)
    case ChangeTodoText(id: Long, newText: String) => TodoTextChanged(id, newText)
  }

  def run(command: Command): Unit = {
    command match {

      case Redo if redoStack.nonEmpty =>
        val event = redoStack.pop()
        undoStack.push(event)
        eventsTopic.distribute(event)


      case Undo if undoStack.nonEmpty =>
        redoStack.push(undoStack.pop())
        eventsTopic.distribute(Reset)
        undoStack.toList.reverse.foreach(x => eventsTopic.distribute(x))

      case todoCommand:TodoCommand =>
        val event = generateEvent(todoCommand)
        redoStack.clear()
        undoStack.push(event)
        eventsTopic.distribute(event)
    }
  }
}



