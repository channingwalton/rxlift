package code.comet

import java.util.Date
import java.util.concurrent.TimeUnit

import com.casualmiracles.rxlift.Components._
import com.casualmiracles.rxlift.{RxCometActor, RxElement}
import net.liftweb.common.Full
import rx.lang.scala.Observable

import scala.concurrent.duration.Duration

class Clock extends RxCometActor {

  override def defaultPrefix = Full("clk")

  // Here is an Observable that will generate a string containing the time every 2 seconds
  val ticker: Observable[String] = Observable.interval(Duration(2, TimeUnit.SECONDS)).map(_ ⇒ new Date().toString)

  // construct a label with the ticker
  val timeLabel: RxElement[String] = label consume ticker

  // send the JsCmds emitted by the label to the actor to send to the UI
  publish(timeLabel)

  // initial render uses the label's ui
  def render = "#time" #> timeLabel.ui
}