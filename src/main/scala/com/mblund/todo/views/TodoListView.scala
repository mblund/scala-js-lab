package com.mblund.todo.views

import com.mblund.todo.domain._
import com.mblund.todo.technology.{Subscriber, Topic}
import com.mblund.todo.technology.technology._

import scalatags.JsDom.all._

class TodoListView(listId: Long, eventsTopic: Topic[TodoEvent], execute: Executor)
  extends View with Subscriber[TodoEvent] {
  eventsTopic.subscribe(this)
  val listElem = div.render
  val isAllDoneView = new IsAllDoneView(listId, eventsTopic, execute)
  val inputField = input(`type` := "text", cls := "form-control", placeholder := "New Todo...").render


  val el =
    div(cls := "col-md-6")(
      div(cls := "panel panel-primary")(
        div(cls := "panel-heading")(
          h3(cls := "panel-title")(s"To-do list $listId")
        ),
        div(cls := "panel-body")(
          form(
            onsubmit := {
              (e:MyHaxEvent) => {               //TODO: check out this
                execute(AddTodo(listId, inputField.value))
                inputField.value = ""
                e.preventDefault()
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
              onclick :=  ( () => execute(RemoveTodoList(listId)) )
            )("Delete To-Do List")
          )
        )
      )
    ).render

  def receive = {

    case TodoAdded(todoId, `listId`, text) =>
      listElem.appendChild(new TodoItemView(todoId, text, eventsTopic, execute).el)

    case TodoListRemoved(`listId`) =>
      removeItself()

  }
}