package code.comet

import com.casualmiracles.rxlift.Components._
import com.casualmiracles.rxlift.{RxCometActor, RxElement}
import net.liftweb.common.Full
import rx.lang.scala.Observable

class Echo extends RxCometActor {

  override def defaultPrefix = Full("echo")

  // an input field whose value is set by an empty stream
  val input = text().run(Observable.empty)

  // a label whose value is obtained from the output stream of the input field above
  val echoLabel: RxElement[String] = label.run(input.values)

  // publish the label. No need to do anything with the input as
  // the label maps input.values, so publishing the label
  // is enough
  publish(echoLabel)

  // initial render uses the input and label's ui
  def render = bind("echo" -> (input.ui ++ echoLabel.ui))

}
