package code.comet

import com.casualmiracles.rxlift.Out
import net.liftweb.http.CometActor
import net.liftweb.http.js.JsCmd
import rx.lang.scala.{Subject, Observable, Subscription}

trait RxCometActor extends CometActor {

  var subscriptions: Seq[Subscription] = Seq.empty
  
  def publish(components: Out[_]*): Unit = {
    components.foreach(o ⇒ handleSubscription(o.jscmd.map(this ! _)))
  }

  def handleSubscription[T](obs: Observable[T]): Unit = handleSubscription(obs.subscribe())

  def handleSubscription(subject: Subscription): Unit = subscriptions = subscriptions :+ subject

  final override def lowPriority : PartialFunction[Any, Unit] = {
    case cmd: JsCmd ⇒ partialUpdate(cmd)
  }

  override def localShutdown() = subscriptions.foreach(_.unsubscribe())
}