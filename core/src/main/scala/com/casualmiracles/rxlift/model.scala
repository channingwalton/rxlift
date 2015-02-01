package com.casualmiracles.rxlift

import net.liftweb.http.js.JsCmd
import rx.lang.scala.Observable

import scala.xml.NodeSeq
import scalaz.Semigroup

case class RxElement[T](values: Observable[T], jscmd: Observable[JsCmd], ui: NodeSeq, id: String)

case class RxComponent[I, O](run: Observable[I] ⇒ RxElement[O])

object RxComponent {
  implicit def ComponentSemiGroup[I, O]: Semigroup[RxComponent[I, O]] = new Semigroup[RxComponent[I, O]] {
    override def append(f1: RxComponent[I, O], f2: ⇒ RxComponent[I, O]): RxComponent[I, O] =
      RxComponent { in ⇒
        val o1: RxElement[O] = f1.run(in)
        val o2: RxElement[O] = f2.run(in)
        RxElement(o1.values.merge(o2.values), o1.jscmd.merge(o2.jscmd), o1.ui ++ o2.ui, Components.genId)
      }
  }
}