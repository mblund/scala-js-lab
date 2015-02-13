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
        override def receive: PartialFunction[TodoEvent, Unit] = {
          case Reset=> events.clear()
          case event:TodoEvent=> events += event }
      })

      val app = new TodoApplication
      app.run(AddTodoList)
      app.run(AddTodoList)
      app.run(AddTodo(1,"Frodo1"))
      assert(events.last match {
        case TodoAdded(_,_,"Frodo1") => true
        case _ => false
      })

      app.run(Undo)
      app.run(AddTodo(1,"Todo1"))
      assert(events.size ==3)
      assert(events.last match {
        case TodoAdded(_,_,"Todo1") => true
        case _ => false
      })

    }
  }
}
