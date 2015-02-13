package com.mblund.todo.views

import com.mblund.todo.domain._
import com.mblund.todo.technology.{View, Subscriber, Topic}
import com.mblund.todo.technology.technology.Executor

import org.scalajs.dom.html._
import scalatags.JsDom.all._

class ApplicationView(implicit val eventsTopic: Topic[TodoEvent], implicit val application: Executor) extends Subscriber[TodoEvent] {
  val todoLists = div().render
  val el =
    div(cls := "container")(
      div(cls := "row")(
        div(cls := "col-md-12")(
          h1("To-do lists"),
          div(cls := "btn-toolbar")(
            div(cls := "btn-group")(
              button(cls := "btn btn-default", onclick := { () => application.run(Undo)})("Undo"),
              button(cls := "btn btn-default", onclick := { () => application.run(Redo)})("Redo"),
              button(cls := "btn btn-primary", onclick := { () => application.run(AddTodoList)})("New to-do list")
            )
          )
        )
      ),
      div(cls := "row")(
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
  val inputField = input(`type` := "text", cls := "form-control", placeholder := "New Todo...").render

  val el =
    div(cls := "col-md-6")(
      div(cls := "panel panel-primary")(
        div(cls := "panel-heading")(
          h3(cls := "panel-title")(s"To-do list $listId")
        ),
        div(cls := "panel-body")(
          form(
            action := "#",
            onsubmit := {
              () => {
                application.run(AddTodo(listId, inputField.value))
                inputField.value = ""
              }
            }
          )(
              div(cls := "input-group")(
                inputField,
                span(cls := "input-group-btn")(
                  button(cls := "btn btn-default", tpe := "submit", value := "Submit")("Add")
                )
              )
            ),
          listElem
        ),
        div(cls := "panel-footer")(
          p(isAllDoneView.el),
          div(cls := "btn-toolbar")(
            button(
              cls := "btn btn-danger",
              onclick := { () => application.run(RemoveTodoList(listId))}
            )("Delete To-Do List")
          )
        )
      )
    ).render

  def receive = {
    case TodoAdded(todoId, `listId`, text) => listElem.appendChild(new TodoItemView(todoId, text).el)
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

class TodoItemView(todoId: Long, text: String)(implicit val eventsTopic: Topic[TodoEvent], implicit val application: Executor)
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
    cls := "form-control",
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
      span(cls := "input-group-btn")(
        button(
          cls := "btn btn-default",
          tpe := "button",
          onclick := { () => application.run(RemoveTodo(todoId))}
        )("Remove")
      )
    ).render

}