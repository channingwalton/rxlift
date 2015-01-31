package com.casualmiracles.rxlift

import net.liftweb.http.CometActor
import net.liftweb.http.js.JsCmd
import rx.lang.scala.{Observable, Subscription}
import scala.collection.mutable.ListBuffer

trait RxCometActor extends CometActor {

  val subscriptions: ListBuffer[Subscription] = ListBuffer.empty[Subscription]
  
  def publish(components: Out[_]*): Unit = {
    components.foreach(o ⇒ handleSubscription(o.jscmd.map(this ! _)))
  }

  def handleSubscription[T](obs: Observable[T]): Unit = subscriptions += obs.subscribe()

  final override def lowPriority : PartialFunction[Any, Unit] = {
    case cmd: JsCmd ⇒ partialUpdate(cmd)
  }

  override def localShutdown() = subscriptions.foreach(_.unsubscribe())
}
