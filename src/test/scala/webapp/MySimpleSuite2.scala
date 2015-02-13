package webapp

import com.mblund.todo.domain.TodoEvent
import com.mblund.todo.technology.{Command, Topic}
import com.mblund.todo.technology.technology.Executor
import com.mblund.todo.views.ApplicationView
import utest._
import utest.assert


object ApplicationTestSuite extends TestSuite {
  val tests = TestSuite{
    'test2{

    }
  }
}

object MyTestSuite extends TestSuite{
  val tests = TestSuite{
    'hello{
      'world{

        try {


          implicit val eventsTopic: Topic[TodoEvent] = new Topic[TodoEvent]

          implicit val executor: Executor = new {
            def run(command: Command): Unit = {
              println("hello")
            }
          }
          println("--c")

          val view = new ApplicationView

//          import org.scalajs.jquery.jQuery
//          jQuery("#click-me-button").click

          println("--d")

          assert(true)
        } catch {
          case e: Exception =>
            println(e)
            assert(false)
        }


      }
    }
    'test2{
      val x = 2
      val y = 2
      assert(x == y )
    }
  }
}