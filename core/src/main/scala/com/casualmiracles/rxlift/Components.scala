package com.casualmiracles.rxlift

import java.util.UUID

import net.liftweb.common.Box
import net.liftweb.http.SHtml._
import net.liftweb.http.js.JsCmds.Run
import net.liftweb.http.js.{JsCmds, JsCmd}
import rx.lang.scala.{Subject, Observable}

import scala.xml.{NodeSeq, Text}

import scalaz.Endo
import scalaz.Lens

object Components {

  type Id = String
  type Attributes = Seq[(String, String)]

  def label: RxComponent[String, String] = RxComponent { (in: Observable[String]) ⇒
    val id = genId
    val js: Observable[JsCmd] = in.map(v ⇒ JsCmds.SetHtml(id, Text(v)))

    // a label does not emit a value so the RxElement's values field is Observable.empty
    RxElement(Observable.empty, js, <span id={id}></span>, id)
  }

  def text(attrs: (String, String)*): RxComponent[String, String] =
    component((s, a) ⇒ ajaxText("", v ⇒ s.onNext(v), a:_*), attrs)

  def textArea(attrs: (String, String)*): RxComponent[String, String] =
    component((s, a) ⇒ ajaxTextarea("", v ⇒ s.onNext(v), a:_*), attrs)

  def select(opts: Seq[SelectableOption[String]], deflt: Box[String], attrs: (String, String)*): RxComponent[String, String] =
    component((s, a) ⇒ ajaxSelect(opts, deflt, v ⇒ s.onNext(v), a:_*), attrs)

  def editable[I, O](component: RxComponent[I, O], edit: Observable[Boolean]): RxComponent[I, O] =
    RxComponent { in ⇒
      val inner: RxElement[O] = component.consume(in)
      val editJsCmds = edit.map(setEditability(inner.id, _))
      RxElement(inner.values, inner.jscmd.merge(editJsCmds), inner.ui, inner.id)
    }

  private def component[O](uiF: (Subject[O], Attributes) => NodeSeq, attrs: Attributes): RxComponent[String, O] =
    RxComponent { (in: Observable[String]) ⇒
      val (id, attributes) = idAndAttrs(attrs)
      val subject = Subject[O]()
      val ui = uiF(subject, attributes)
      val js: Observable[JsCmd] = in.map(v ⇒ JsCmds.SetValById(id, v))

      RxElement(subject, js, ui, id)
    }

  private def setProp(id: String, property: String, value: String): JsCmd = Run(s"$$('#$id').prop('$property', $value)")

  private def setEditability(id: String, editable: Boolean): JsCmd = setProp(id, "disabled", (!editable).toString)

  def genId: Id = UUID.randomUUID().toString

  private def idAndAttrs(attrs: Attributes): (Id, Attributes) =
    attrs.find(_._1.toLowerCase == "id").fold(createIdAndAttrs(attrs))(id ⇒ (id._1, attrs))

  private def createIdAndAttrs(attrs: Attributes): (Id, Attributes) = {
    val id = genId
    (id, attrs :+ ("id", id))
  }

  def focus[T, F](component: RxComponent[F, F], lens: Lens[T, F]): RxComponent[T, Endo[T]] =
    RxComponent { in: Observable[T] ⇒
      val fObs: Observable[F] = in.map(lens.get)
      val element: RxElement[F] = component.consume(fObs)
      val endoOut: Observable[Endo[T]] = element.values.map((f: F) ⇒ Endo((t: T) ⇒ lens.set(t, f)))
      RxElement(endoOut, element.jscmd, element.ui, element.id)
    }
}
