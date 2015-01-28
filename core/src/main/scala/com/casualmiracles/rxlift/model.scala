package com.casualmiracles.rxlift

import net.liftweb.http.js.JsCmd
import rx.lang.scala.Observable

import scala.xml.NodeSeq

case class Out[T](values: Observable[T], jscmd: Observable[JsCmd], ui: NodeSeq, id: Option[String])

case class Component[I, O](run: Observable[I] ⇒ Out[O])