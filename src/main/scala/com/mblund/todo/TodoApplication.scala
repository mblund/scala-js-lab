package com.mblund.todo

import com.mblund.todo.domain.{AddTodoList, TodoApplication, TodoEvent}
import com.mblund.todo.technology.Topic
import com.mblund.todo.views.ApplicationView

import scala.scalajs.js.JSApp

object TodoApplication extends JSApp {

  import org.scalajs.dom.document

  def main() = {
    implicit val eventsTopic: Topic[TodoEvent] = new Topic[TodoEvent]
    implicit val application = new TodoApplication
    val applicationView = new ApplicationView()
    document.body.appendChild(applicationView.el)
    application.run(AddTodoList)
    application.run(AddTodoList)
  }
}
