package com.casualmiracles.rxlift

import net.liftweb.http.js.JsCmd
import rx.lang.scala.Observable

import scala.xml.NodeSeq

case class RxElement[T](values: Observable[T], jscmd: Observable[JsCmd], ui: NodeSeq, id: String)

case class RxComponent[I, O](consume: Observable[I] ⇒ RxElement[O]) {
  def +[U <: I, V >: O](other: RxComponent[U, V]): RxComponent[U, V] =
    RxComponent { in ⇒
      val o1: RxElement[O] = consume(in)
      val o2: RxElement[V] = other.consume(in)
      RxElement(o1.values.merge(o2.values), o1.jscmd.merge(o2.jscmd), o1.ui ++ o2.ui, Components.genId)
    }

  def mapUI(f: NodeSeq ⇒ NodeSeq): RxComponent[I, O] = RxComponent { in ⇒
    val e = consume(in)
    RxElement(e.values, e.jscmd, f(e.ui), e.id)
  }
}
