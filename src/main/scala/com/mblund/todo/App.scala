package com.mblund.todo

import com.mblund.todo.domain.TodoEvent
import com.mblund.todo.technology.Topic
import com.mblund.todo.views.ApplicationView

import scala.scalajs.js.JSApp

object App extends JSApp {

  import org.scalajs.dom.document

  def main() = {
    val eventsTopic: Topic[TodoEvent] = new Topic[TodoEvent]
    val application = new domain.TodoApplication(eventsTopic)
    val applicationView = new ApplicationView(eventsTopic, application.run)
    document.body.appendChild(applicationView.el)

    val persistence = new LocalStorageSubscriber
    eventsTopic.subscribe(persistence)

    persistence.load().foreach(eventsTopic.distribute)
  }
}
