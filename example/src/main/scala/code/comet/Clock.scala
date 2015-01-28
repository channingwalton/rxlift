package code.comet

import java.util.Date
import java.util.concurrent.TimeUnit

import com.casualmiracles.rxlift.Components._
import com.casualmiracles.rxlift.Out
import net.liftweb.common.Full
import net.liftweb.http.CometActor
import net.liftweb.http.js.JsCmd
import rx.lang.scala.Observable

import scala.concurrent.duration.Duration

class Clock extends CometActor {

  override def defaultPrefix = Full("clk")

  // Here is an Observable that will generate a string containing the time every 2 seconds
  val ticker: Observable[String] = Observable.interval(Duration(2, TimeUnit.SECONDS)).map(_ ⇒ new Date().toString)

  // construct a label with the ticker
  val timeLabel: Out[String] = label.run(ticker)

  // send the JsCmds emitted by the label to the actor to send to the UI
  val subscription = timeLabel.jscmd.map(this ! _).subscribe()

  // initial render uses the label's ui
  def render = bind("time" -> timeLabel.ui)

  override def lowPriority : PartialFunction[Any, Unit] = {
    case cmd: JsCmd ⇒ partialUpdate(cmd)
  }

  // important to unsubscribe to subscriptions
  override def localShutdown() = subscription.unsubscribe()
}