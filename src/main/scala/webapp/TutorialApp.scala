package webapp

import org.scalajs.dom.html.Input

import webapp.teknik._
import webapp.teknik.Subscriber
import webapp.teknik.teknik.Executor
import scala.scalajs.js.JSApp
import scalatags.JsDom.all._

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

  class ApplicationView(implicit val eventsTopic: Topic[TodoEvent], implicit val application: Executor) extends Subscriber[TodoEvent] {
    val todoLists = div().render
    val el =
      div(cls:="container")(
        div(cls:="row")(
          div(cls:="col-md-12")(
            h1("To-do lists"),
            div(cls:="btn-toolbar")(
              div(cls:="btn-group")(
                button(cls:="btn btn-default",  onclick := { () => application.run(Undo)})("Undo"),
                button(cls:="btn btn-default",  onclick := { () => application.run(Redo)})("Redo"),
                button(cls:="btn btn-primary",  onclick := { () => application.run(AddTodoList)})("New to-do list")
              )
            )

          )
        ),
        div(cls:="row")(
          todoLists
        )
      ).render

    def receive = {
      case TodoListAdded(todoListId) => {
        todoLists.appendChild(new TodoListView(todoListId).el)
      }
      case Reset => todoLists.innerHTML = ""
    }
  }

  class TodoListView(listId: Long)(implicit val eventsTopic: Topic[TodoEvent], implicit val application: Executor)
    extends View with Subscriber[TodoEvent] {

    val listElem = div.render
    val isAllDoneView = new IsAllDoneView(listId)
    val inputField = input(`type` := "text", cls:="form-control", placeholder:="New Todo...").render

    val el =
      div(cls:="col-md-6")(
        div(cls := "panel panel-primary")(
          div(cls:="panel-heading")(
            h3(cls:="panel-title")(s"To-do list $listId")
          ),
          div(cls:="panel-body")(
            form(
              action := "#",
              onsubmit := {
                () => {
                  application.run(AddTodo(listId, inputField.value))
                  inputField.value = ""
                }
              }
            )(
                div(cls :="input-group")(
                  inputField,
                  span(cls:="input-group-btn")(
                    button(cls:="btn btn-default", tpe:="submit", value:="Submit")("Add")
                  )
                )
              ),
            listElem
          ),
          div(cls:="panel-footer")(
            p(isAllDoneView.el),
            div( cls:="btn-toolbar")(
              button(
                cls:="btn btn-danger",
                onclick := { () => application.run(RemoveTodoList(listId))}
              )("Delete To-Do List")
            )
          )
        )
      ).render

    def receive = {
      case TodoAdded(todoId, `listId`, text) => listElem.appendChild(new TodoView(todoId, text).el)
      case TodoListRemoved(`listId`) => removeItself()
    }
  }

  class IsAllDoneView(val listId: Long)(implicit val eventsTopic: Topic[TodoEvent], implicit val application: Executor)
    extends View with Subscriber[TodoEvent] {
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
      val nrFinishedTodos = todos.values.filter(_ == true).size
      el.textContent = s"${nrFinishedTodos} of  ${todos.size} to-dos are done."
    }
  }

  class TodoView(todoId: Long, text: String)(implicit val eventsTopic: Topic[TodoEvent], implicit val application: Executor)
    extends View with Subscriber[TodoEvent] {

    def receive = {
      case TodoRemoved(`todoId`) => {
        removeItself()
        eventsTopic.unsubscribe(this)
      }
      case TodoStatusChanged(`todoId`, status) => checkboxElem.checked = status
      case TodoTextChanged(`todoId`, text) => inputElem.value = text
    }
    val checkboxElem: Input = input(
      tpe := "checkbox",
      onchange := { () => application.run(ChangeTodoStatus(todoId, checkboxElem.checked))}
    ).render

    val inputElem: Input = input(
      cls:="form-control",
      tpe := "text",
      value := text,
      onchange := { () => application.run(ChangeTodoText(todoId, inputElem.value))}
    ).render

    val el =
      div(`class` := "input-group")(
        span(`class` := "input-group-addon")(
          checkboxElem
        ),
        inputElem,
        span(cls:="input-group-btn")(
          button(
            cls:="btn btn-default",
            tpe:="button",
            onclick := { () => application.run(RemoveTodo(todoId))}
          )("Remove")
        )
      ).render

  }

}
