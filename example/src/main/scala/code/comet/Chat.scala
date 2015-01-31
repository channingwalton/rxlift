package code.comet

import com.casualmiracles.rxlift.Components._
import com.casualmiracles.rxlift.{RxCometActor, Out}
import net.liftweb.common.Full

import rx.lang.scala.{Observable, Subject}
import rx.lang.scala.subjects.BehaviorSubject

case class Message(username: String, msg: String)

object Chat {
  val send: Subject[Message] = BehaviorSubject[Message](Message("System", "Welcome"))
  val allMessages: Observable[Seq[Message]] = send.scan(Seq.empty[Message])(_ :+ _)
}

class Chat extends RxCometActor {

  override def defaultPrefix = Full("chat")

  // whose talking?
  val username = text.run(Observable.empty)

  // what are they saying?
  val msgObservable = Subject[String]()
  val msg = text.run(msgObservable)

  // combine the username and messages and map it to a Message
  val messages = username.values.combineLatest(msg.values).map{ case (u, m) ⇒ Message(u, m) }

  // send the user's message to the 'chat server' and blank the msg field
  val messageDistributor = messages.map(m ⇒ {
    Chat.send.onNext(m)
    msgObservable.onNext("")
  })

  // subscribe to the distributor
  handleSubscription(messageDistributor)

  // a textarea whose content is obtained from Chat.messages
  def msgLine(msgs: Seq[Message]): String = msgs.map(m ⇒ m.username + ": " + m.msg).mkString("\n")
  val allMessages: Out[String] = textArea.run(Chat.allMessages.map(msgLine))

  publish(allMessages, username, msg)

  def render = bind("chat", "messages" -> allMessages.ui, "username" -> username.ui,  "msg" -> msg.ui)

}