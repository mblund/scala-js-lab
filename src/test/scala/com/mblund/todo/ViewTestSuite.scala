package com.mblund.todo

import com.mblund.todo.domain.{TodoListAdded, TodoEvent}
import com.mblund.todo.technology.technology.Executor

import com.mblund.todo.technology.{Command, Topic}
import com.mblund.todo.views.ApplicationView
import utest.{assert, _}

object ViewTestSuite extends TestSuite{
  val tests = TestSuite{
    'argh{
      "The application view render a new to-do list after an TodoListAdded event " - {

        val topic: Topic[TodoEvent] = new Topic[TodoEvent]
        val executor:Executor = _ => ()

        val view = new ApplicationView(topic, executor)
        assert( view.el.querySelector(".panel-title") == null )
        topic.distribute(TodoListAdded(0))
        assert( view.el.querySelector(".panel-title") != null )
      }
      "Failing test in Rhino, 64k function size limit" - {
        try {
          val topic: Topic[TodoEvent] = new Topic[TodoEvent]
          val executor:Executor = _ => ()

          val view = new ApplicationView(topic, executor)
          assert(true)
        } catch {
          case e: Exception =>
            println(e)
            assert(false)
        }
      }
    }

  }
}