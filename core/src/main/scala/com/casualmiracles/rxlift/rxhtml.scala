package com.casualmiracles.rxlift

import java.util.UUID

import net.liftweb.http.SHtml
import net.liftweb.http.js.{JsCmds, JsCmd}
import rx.lang.scala.{Subject, Observable}
import net.liftweb.http.SHtml.ElemAttr._

object RxHtml {

  private def genId: String = UUID.randomUUID().toString

  def label: Component[String, String] = Component { (in: Observable[String]) ⇒
    val id = genId
    val js: Observable[JsCmd] = in.map(v ⇒ JsCmds.SetValById(id, v))

    // a label does not emit a value so Out.values is empty
    Out(Observable.empty, js, <span id={id}></span>, Some(id))
  }

  def textarea: Component[String, String] = Component { (in: Observable[String]) ⇒
    val id = genId
    val subject = Subject[String]()
    val ui = SHtml.ajaxText("", subject.onNext(_), "id" → id)
    val js: Observable[JsCmd] = in.map(v ⇒ JsCmds.SetValById(id, v))

    Out(subject, js, ui, Some(id))
  }
}