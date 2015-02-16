package com.mblund.todo

import com.mblund.todo.domain._
import com.mblund.todo.technology.{Subscriber, Topic}
import utest.{assert, _}

import scala.collection.mutable


import upickle._

case class Thing(a: Int, b: String)

sealed trait IntOrTuple
case class IntThing(i: Int) extends IntOrTuple
case class TupleThing(name: String, t: (Int, Int)) extends IntOrTuple

case class Big(i: Int, b: Boolean, str: String, c: Char, d: Double)
case class Foo(i: Int = 10, s: String = "lol")

object PickleTestSuite extends TestSuite {
  val tests = TestSuite{
    'Pickle{
      'write1 {
        val a = write(IntThing(1))
        assert(a == """["com.mblund.todo.IntThing",{"i":1}]""")
      }
      'write2{
        val b = write(TupleThing("naeem", (1, 2)))
        assert (b == """["com.mblund.todo.TupleThing",{"name":"naeem","t":[1,2]}]""")
      }
      'read1{
        val result = read[IntOrTuple]("""["com.mblund.todo.IntThing", {"i": 1}]""")
        assert (result == IntThing(1))
      }
      'read2{
        // this way you can read tagged value without knowing its type in advance, just use type of the sealed trait
        read[IntOrTuple]("""["com.mblund.todo.IntThing", {"i": 1}]""")
      }
      'write3{
        val result = write(Thing(1, "gg"))
        assert ( result == """{"a":1,"b":"gg"}""")
      }
    }
  }
}
