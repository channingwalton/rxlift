package com.casualmiracles.rxlift

import net.liftweb.http.js.JsCmd
import rx.lang.scala.Observable

import scala.xml.NodeSeq
import scalaz.Semigroup

case class Out[T](values: Observable[T], jscmd: Observable[JsCmd], ui: NodeSeq, id: Option[String])

case class Component[I, O](run: Observable[I] ⇒ Out[O])

object Component {
  implicit def ComponentSemiGroup[I, O]: Semigroup[Component[I, O]] = new Semigroup[Component[I, O]] {
    override def append(f1: Component[I, O], f2: ⇒ Component[I, O]): Component[I, O] =
      Component { in ⇒
        val o1: Out[O] = f1.run(in)
        val o2: Out[O] = f2.run(in)
        Out(o1.values.merge(o2.values), o1.jscmd.merge(o2.jscmd), o1.ui ++ o2.ui, None)
      }
  }
}