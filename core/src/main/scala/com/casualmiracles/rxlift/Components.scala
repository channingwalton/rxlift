package com.casualmiracles.rxlift

import java.util.UUID

import net.liftweb.http.SHtml
import net.liftweb.http.js.JsCmds.Run
import net.liftweb.http.js.{JsCmds, JsCmd}
import rx.lang.scala.{Subject, Observable}

import scala.xml.Text

object Components {
  private def genId: String = UUID.randomUUID().toString

  def label: Component[String, String] = Component { (in: Observable[String]) ⇒
    val id = genId
    val js: Observable[JsCmd] = in.map(v ⇒ JsCmds.SetHtml(id, Text(v)))

    // a label does not emit a value so Out.values is empty
    Out(Observable.empty, js, <span id={id}></span>, Some(id))
  }

  def text: Component[String, String] = Component { (in: Observable[String]) ⇒
    val id = genId
    val subject = Subject[String]()
    val ui = SHtml.ajaxText("", subject.onNext(_), "id" → id)
    val js: Observable[JsCmd] = in.map(v ⇒ JsCmds.SetValById(id, v))

    Out(subject, js, ui, Some(id))
  }

  def textArea: Component[String, String] = Component { (in: Observable[String]) ⇒
    val id = genId
    val subject = Subject[String]()
    val ui = SHtml.ajaxTextarea("", subject.onNext(_), "id" → id)
    val js: Observable[JsCmd] = in.map(v ⇒ JsCmds.SetValById(id, v))

    Out(subject, js, ui, Some(id))
  }

  def setProp(id: String, property: String, value: Boolean): JsCmd = Run(s"$$('#$id').prop('$property', $value)")

  def setEditability(id: String, editable: Boolean): JsCmd = setProp(id, "disabled", !editable)

  def editable[I, O](component: Component[I, O], edit: Observable[Boolean]): Component[I, O] =
    Component { in ⇒
      val inner: Out[O] = component.run(in)
      val (newUI, id) = inner.id match {
        case Some(existingId) ⇒ (inner.ui, existingId)
        case None ⇒
          val newId = UUID.randomUUID().toString
          (<div id={newId}>{inner.ui}</div>, newId)
      }

      val editJsCmds = edit.map(setEditability(id, _))
      Out(inner.values, inner.jscmd.merge(editJsCmds), newUI, Some(id))
    }
}