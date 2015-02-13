package webapp

import webapp.teknik._
import scala.scalajs.js.JSApp

object TutorialApp extends JSApp {

  import org.scalajs.dom.document

  def main() = {
    implicit val eventsTopic: Topic[TodoEvent] = new Topic[TodoEvent]
    implicit val application = new TodoApplication
    val applicationView = new ApplicationView()
    document.body.appendChild(applicationView.el)
    application.run(AddTodoList)
    application.run(AddTodoList)
  }

  case object Undo extends Command
  case object Redo extends Command

  sealed trait TodoCommand extends Command
  case object AddTodoList extends TodoCommand
  case class RemoveTodoList(listId: Long) extends TodoCommand
  case class AddTodo(listId: Long, text: String) extends TodoCommand
  case class RemoveTodo(id: Long) extends TodoCommand
  case class ChangeTodoStatus(id: Long, newStatus: Boolean) extends TodoCommand
  case class ChangeTodoText(id: Long, newText: String) extends TodoCommand
  
  sealed trait TodoEvent
  case class TodoListAdded(id: Long) extends TodoEvent
  case class TodoListRemoved(id: Long) extends TodoEvent
  case class TodoAdded(id: Long, listId: Long, text: String) extends TodoEvent
  case class TodoRemoved(id: Long) extends TodoEvent
  case class TodoStatusChanged(todoId: Long, newStatus: Boolean) extends TodoEvent
  case class TodoTextChanged(todoId: Long, newText: String) extends TodoEvent
  case object Reset extends TodoEvent

  class TodoApplication()(implicit val eventsTopic: Topic[TodoEvent]) {
    private val undoStack = new scala.collection.mutable.ArrayStack[TodoEvent]()
    private val redoStack = new scala.collection.mutable.ArrayStack[TodoEvent]()

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
        case Redo => if (redoStack.nonEmpty) {
          val event = redoStack.pop()
          undoStack.push(event) //TODO:den här hade jag missat. Gör ett test som failar om den här är borta
          eventsTopic.distribute(event)
        }
        case Undo => if (undoStack.nonEmpty) {
          redoStack.push(undoStack.pop())
          eventsTopic.distribute(Reset)
          undoStack.toList.reverse.foreach(x => eventsTopic.distribute(x))
        }
        case todoCommand:TodoCommand =>
          val event = generateEvent(todoCommand)
          redoStack.clear()
          undoStack.push(event)
          eventsTopic.distribute(event)
      }
    }
  }

}
