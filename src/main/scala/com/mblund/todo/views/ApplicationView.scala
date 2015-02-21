package com.mblund.todo.views

import com.mblund.todo.domain._
import com.mblund.todo.technology.{Subscriber, Topic}
import com.mblund.todo.technology.technology._

import scalatags.JsDom.all._

class ApplicationView(eventsTopic: Topic[TodoEvent], execute: Executor) extends Subscriber[TodoEvent] {

  eventsTopic.subscribe(this)

  val todoLists = div().render
  val el =
    div(cls := "container")(
      div(cls := "row")(
        div(cls := "col-md-12")(
          h1("To-do lists"),
          div(cls := "btn-toolbar")(
            div(cls := "btn-group")(
              button(cls := "btn btn-default", onclick := { () => execute(Undo)})("Undo"),
              button(cls := "btn btn-default", onclick := { () => execute(Redo)})("Redo"),
              button(cls := "btn btn-primary", onclick := { () => execute(AddTodoList)})("New to-do list")
            )
          )
        )
      ),
      div(cls := "row")(
        todoLists
      )
    ).render

  def receive = {
    case TodoListAdded(todoListId) =>
      todoLists.appendChild(new TodoListView(todoListId, eventsTopic, execute).el)

    case Reset => todoLists.innerHTML = ""
  }

}