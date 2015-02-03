package com.casualmiracles.rxlift

import java.util.UUID

import net.liftweb.common.Box
import net.liftweb.http.SHtml
import net.liftweb.http.SHtml.SelectableOption
import net.liftweb.http.js.JsCmds.Run
import net.liftweb.http.js.{JsCmds, JsCmd}
import rx.lang.scala.{Subject, Observable}

import scala.xml.Text

object Components {
  type Id = String

  def genId: Id = UUID.randomUUID().toString

  private def createIdAndAttrs(attrs: Seq[(String, String)]): (Id, Seq[(String, String)]) = {
    val id = genId
    (id, attrs :+ ("id", id))
  }

  private def idAndAttrs(attrs: Seq[(String, String)]): (Id, Seq[(String, String)]) =
    attrs.find(_._1.toLowerCase == "id").fold(createIdAndAttrs(attrs))(id ⇒ (id._1, attrs))

  def label: RxComponent[String, String] = RxComponent { (in: Observable[String]) ⇒
    val id = genId
    val js: Observable[JsCmd] = in.map(v ⇒ JsCmds.SetHtml(id, Text(v)))

    // a label does not emit a value so Out.values is empty
    RxElement(Observable.empty, js, <span id={id}></span>, id)
  }

  def text(attrs: (String, String)*): RxComponent[String, String] = RxComponent { (in: Observable[String]) ⇒
    val (id, attributes) = idAndAttrs(attrs)
    val subject = Subject[String]()
    val ui = SHtml.ajaxText("", v ⇒ subject.onNext(v), attributes:_*)
    val js: Observable[JsCmd] = in.map(v ⇒ JsCmds.SetValById(id, v))

    RxElement(subject, js, ui, id)
  }

  def textArea(attrs: (String, String)*): RxComponent[String, String] = RxComponent { (in: Observable[String]) ⇒
    val (id, attributes) = idAndAttrs(attrs)
    val subject = Subject[String]()
    val ui = SHtml.ajaxTextarea("", v ⇒ subject.onNext(v), attributes:_*)
    val js: Observable[JsCmd] = in.map(v ⇒ JsCmds.SetValById(id, v))

    RxElement(subject, js, ui, id)
  }

  def select(opts: Seq[SelectableOption[String]], deflt: Box[String], attrs: (String, String)*): RxComponent[String, String] = RxComponent { (in: Observable[String]) ⇒
    val (id, attributes) = idAndAttrs(attrs)
    val subject = Subject[String]()
    val ui = SHtml.ajaxSelect(opts, deflt, v ⇒ subject.onNext(v), attributes:_*)
    val js: Observable[JsCmd] = in.map(v ⇒ JsCmds.SetValById(id, v))

    RxElement(subject, js, ui, id)
  }

  def editable[I, O](component: RxComponent[I, O], edit: Observable[Boolean]): RxComponent[I, O] =
    RxComponent { in ⇒
      val inner: RxElement[O] = component.consume(in)
      val editJsCmds = edit.map(setEditability(inner.id, _))
      RxElement(inner.values, inner.jscmd.merge(editJsCmds), inner.ui, inner.id)
    }

  private def setProp(id: String, property: String, value: String): JsCmd = Run(s"$$('#$id').prop('$property', $value)")

  private def setEditability(id: String, editable: Boolean): JsCmd = setProp(id, "disabled", (!editable).toString)

}