package com.casualmiracles.rxlift

import net.liftweb.http.js.JsCmd
import rx.lang.scala.Observable

import scala.xml.NodeSeq

/**
  * RxElement is a reactive UI component
  * @param values the values emitted by the UI
  * @param jscmd the JsCmds produced by the component to update the UI in response to changes to its input
  * @param ui the HTML to render this component
  * @param id the HTML id of this component
  */
case class RxElement[T](values: Observable[T], jscmd: Observable[JsCmd], ui: NodeSeq, id: String)

/**
  * An RxComponent is a builder of RxElements.
  * @param consume a function of an Observable[I] returning an RxElement[O]
  * @tparam I the input observable's type
  * @tparam O the RxElement's type
  */
case class RxComponent[I, O](consume: Observable[I] ⇒ RxElement[O]) {

  /**
    * Compose this component with another
    */
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
