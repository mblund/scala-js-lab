package com.mblund.todo

import com.mblund.todo.domain._
import com.mblund.todo.technology.{Subscriber, Command, Topic}
import utest.{assert, _}

import scala.collection.mutable


object ApplicationTestSuite extends TestSuite {
  val tests = TestSuite{
    'Undo{

      implicit val eventsTopic = new Topic[TodoEvent]
      val events = new mutable.MutableList[TodoEvent]()
      eventsTopic.subscribe(new Subscriber[TodoEvent] {
        override def receive = {
          case Reset=> events.clear()
          case event:TodoEvent=> events += event }
      })

      val application = new TodoApplication
      application.run(AddTodoList)
      application.run(AddTodoList)
      application.run(AddTodo(1,"Frodo1"))
      assert(events.last match {
        case TodoAdded(_,_,"Frodo1") => true
        case _ => false
      })

      application.run(Undo)
      application.run(AddTodo(1,"Todo1"))

      assert(events.size == 3)
      assert(events.last match {
        case TodoAdded(_,_,"Todo1") => true
        case _ => false
      })

    }
  }
}
