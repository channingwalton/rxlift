package code.comet

import com.casualmiracles.rxlift.Components._
import com.casualmiracles.rxlift.Out
import net.liftweb.common.Full
import net.liftweb.http.CometActor
import net.liftweb.http.js.JsCmd
import rx.lang.scala.Observable

class Echo extends CometActor {

  override def defaultPrefix = Full("echo")

  // an input field whose value is set by an empty stream
  val input = text.run(Observable.empty)

  // a label whose value is obtained from the output stream of the input field above
  val echoLabel: Out[String] = label.run(input.values)

  // send the JsCmds emitted by the label to the actor to send to the UI
  val subscription = echoLabel.jscmd.map(this ! _).subscribe()

  // initial render uses the input and label's ui
  def render = bind("echo" -> (input.ui ++ echoLabel.ui))

  // receive the JsCmd sent by the above and send it to the ui
  override def lowPriority : PartialFunction[Any, Unit] = {
    case cmd: JsCmd ⇒ partialUpdate(cmd)
  }

  // important to unsubscribe to subscriptions
  override def localShutdown() = subscription.unsubscribe()
}
