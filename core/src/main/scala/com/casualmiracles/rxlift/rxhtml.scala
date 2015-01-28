package com.casualmiracles.rxlift

import java.util.UUID

import net.liftweb.http.js.{JsCmds, JsCmd}
import rx.lang.scala.Observable

object RxHtml {

  def label: Component[String, Nothing] = Component { (in: Observable[String]) ⇒
    val id = UUID.randomUUID().toString
    val jsout: Observable[JsCmd] = in.map(v ⇒ JsCmds.SetValById(id, v))

    // a label does not emit a value so Out.values is empty
    Out(Observable.empty, jsout, <span id={id}></span>, Some(id))
  }
}