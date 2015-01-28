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

  val ticker: Observable[String] = Observable.interval(Duration(2, TimeUnit.SECONDS)).map(_ ⇒ new Date().toString)
  val timeLabel: Out[String] = label.run(ticker)
  timeLabel.jscmd.map(this ! _).subscribe()

  def render = bind("time" -> timeLabel.ui)

  override def lowPriority : PartialFunction[Any, Unit] = {
    case cmd: JsCmd ⇒ partialUpdate(cmd)
  }
}