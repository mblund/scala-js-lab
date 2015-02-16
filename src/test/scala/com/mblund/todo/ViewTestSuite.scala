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

        implicit val eventsTopic: Topic[TodoEvent] = new Topic[TodoEvent]
        implicit val executor:Executor = new { def run(command: Command): Unit = {} }

        val view = new ApplicationView
        assert( view.el.querySelector(".panel-title") == null )
        eventsTopic.distribute(TodoListAdded(0))
        assert( view.el.querySelector(".panel-title") != null )
      }
      "Failing test in Rhino, 64k function size limit" - {
        try {
          implicit val eventsTopic = new Topic[TodoEvent]
          implicit val executor = new { def run(command: Command): Unit = {} }
          val view = new ApplicationView
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