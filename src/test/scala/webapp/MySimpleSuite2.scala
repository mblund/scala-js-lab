package webapp

import utest._
import utest.assert
import webapp.TutorialApp._
import webapp.teknik.{Command, Topic}


object FooTestSuite extends TestSuite {
  val tests = TestSuite{
    'test2{
      val x = 1
      val y = 1
      assert(x == y)
    }
  }
}
object MyTestSuite extends TestSuite{
  val tests = TestSuite{
    'hello{
      'world{

        try {
          println("--a")

          implicit val eventsTopic: Topic[TodoEvent] = new Topic[TodoEvent]
          println("--b")

          implicit val executor: webapp.teknik.teknik.Executor = new {
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