package com.casualmiracles.rxlift

import net.liftweb.http.CometActor
import net.liftweb.http.js.JsCmd
import rx.lang.scala.{Observable, Subscription}
import scala.collection.mutable.ListBuffer

trait RxCometActor extends CometActor {

  // set of Rx subscriptions that need to be unsubscribed to when the actor dies
  val subscriptions: ListBuffer[Subscription] = ListBuffer.empty[Subscription]

  // publish each RxElement's jscmd by sending values from the stream to
  // this actor for sending to the client
  def publish(components: RxElement[_]*): Unit =
    components.foreach(o ⇒ handleSubscription(o.jscmd.map(partialUpdate(_))))

  // convenient method to subscribe to an Observable and manage the subscription 
  def handleSubscription[T](obs: Observable[T]): Unit = subscriptions += obs.subscribe()

  // unsubscribe to all subscriptions
  override def localShutdown() = subscriptions.foreach(_.unsubscribe())
}
