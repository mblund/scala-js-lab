package com.mblund.todo.views

import org.scalajs.dom._

trait View {
  def el: Element

  def removeItself() = {
    if (el.parentNode != null) {
      el.parentNode.removeChild(el)
    }
  }
}
