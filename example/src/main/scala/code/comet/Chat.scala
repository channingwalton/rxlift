package code.comet

import com.casualmiracles.rxlift.Components._
import com.casualmiracles.rxlift.Out
import net.liftweb.common.Full

import rx.lang.scala.{Observable, Subject}
import rx.lang.scala.subjects.BehaviorSubject

object Chat {
  val send: Subject[String] = BehaviorSubject[String]("Welcome")
  val allMessages: Observable[String] = send.scan("")((a, b) ⇒ s"$a\n$b")
}

class Chat extends RxCometActor {

  override def defaultPrefix = Full("chat")

  val inputObservable = Subject[String]()
  val input = text.run(inputObservable)

  // a textarea whose content is obtained from Chat.messages
  val messageArea: Out[String] = textArea.run(Chat.allMessages)

  // send the user's message to everyone and blank their input field
  val messageDistributor = input.values.map(m ⇒ {Chat.send.onNext(m); inputObservable.onNext("")} )

  // subscribe to the distributor
  handleSubscription(messageDistributor)

  publish(messageArea, input)

  def render = bind("chat", "messages" -> messageArea.ui, "input" -> input.ui)

}

