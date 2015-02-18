package com.mblund.todo.views

import com.mblund.todo.domain._
import com.mblund.todo.technology.{Subscriber, Topic}
import com.mblund.todo.technology.technology._
import org.scalajs.dom.html._

import scalatags.JsDom.all._

class TodoItemView(todoId: Long, text: String, eventsTopic: Topic[TodoEvent], execute: Executor)
  extends View with Subscriber[TodoEvent] {
  eventsTopic.subscribe(this)

  def receive = {

    case TodoRemoved(`todoId`) =>
      removeItself()
      eventsTopic.unsubscribe(this)

    case TodoStatusChanged(`todoId`, status) =>
      checkboxElem.checked = status

    case TodoTextChanged(`todoId`, text) =>
      inputElem.value = text

  }

  val checkboxElem: Input = input(
    tpe := "checkbox",
    onchange := { () => execute(ChangeTodoStatus(todoId, checkboxElem.checked))}
  ).render

  val inputElem: Input = input(
    cls := "form-control",
    tpe := "text",
    value := text,
    onchange := { () => execute(ChangeTodoText(todoId, inputElem.value))}
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
          onclick := { () => execute(RemoveTodo(todoId))}
        )("Remove")
      )
    ).render

}