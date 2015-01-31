package com.casualmiracles.rxlift

import java.util.UUID

import net.liftweb.http.SHtml
import net.liftweb.http.js.JsCmds.Run
import net.liftweb.http.js.{JsCmds, JsCmd}
import rx.lang.scala.{Subject, Observable}

import scala.xml.Text

object Components {
  private def genId: String = UUID.randomUUID().toString

  def label: RxComponent[String, String] = RxComponent { (in: Observable[String]) ⇒
    val id = genId
    val js: Observable[JsCmd] = in.map(v ⇒ JsCmds.SetHtml(id, Text(v)))

    // a label does not emit a value so Out.values is empty
    RxElement(Observable.empty, js, <span id={id}></span>, Some(id))
  }

  def text: RxComponent[String, String] = RxComponent { (in: Observable[String]) ⇒
    val id = genId
    val subject = Subject[String]()
    val ui = SHtml.ajaxText("", v ⇒ subject.onNext(v), "id" → id)
    val js: Observable[JsCmd] = in.map(v ⇒ JsCmds.SetValById(id, v))

    RxElement(subject, js, ui, Some(id))
  }

  def textArea: RxComponent[String, String] = RxComponent { (in: Observable[String]) ⇒
    val id = genId
    val subject = Subject[String]()
    val ui = SHtml.ajaxTextarea("", v ⇒ subject.onNext(v), "id" → id)
    val js: Observable[JsCmd] = in.map(v ⇒ JsCmds.SetValById(id, v))

    RxElement(subject, js, ui, Some(id))
  }

  def editable[I, O](component: RxComponent[I, O], edit: Observable[Boolean]): RxComponent[I, O] =
    RxComponent { in ⇒
      val inner: RxElement[O] = component.run(in)
      val (newUI, id) = inner.id match {
        case Some(existingId) ⇒ (inner.ui, existingId)
        case None ⇒
          val newId = UUID.randomUUID().toString
          (<div id={newId}>{inner.ui}</div>, newId)
      }

      val editJsCmds = edit.map(setEditability(id, _))
      RxElement(inner.values, inner.jscmd.merge(editJsCmds), newUI, Some(id))
    }

  private def setProp(id: String, property: String, value: String): JsCmd = Run(s"$$('#$id').prop('$property', $value)")

  private def setEditability(id: String, editable: Boolean): JsCmd = setProp(id, "disabled", (!editable).toString)

}