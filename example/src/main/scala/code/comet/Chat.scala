package code.comet

import com.casualmiracles.rxlift.Components._
import com.casualmiracles.rxlift.{RxCometActor, RxElement}
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
  val username = text().run(Observable.empty)

  // make an initially disabled text component, enabled by the username being set
  val msgEditable = Observable.just(false).merge(username.values.map(_.trim.nonEmpty))
  val editableMsg = editable(text(), msgEditable)

  // run the editableMsh with an input observable used below to reset the input field
  val msgIn = Subject[String]()
  val msg = editableMsg.run(msgIn)

  // combine the username and messages and map it to a Message
  val messages = username.values.combineLatest(msg.values).map{ case (u, m) ⇒ Message(u, m) }

  // send the user's message to the 'chat server' and blank the msg field
  val messageDistributor = messages.map(m ⇒ {
    Chat.send.onNext(m)
    msgIn.onNext("")
  })

  // subscribe to the distributor
  handleSubscription(messageDistributor)

  // a textarea whose content is obtained from Chat.messages
  def msgLine(msgs: Seq[Message]): String = msgs.map(m ⇒ m.username + ": " + m.msg).mkString("\n")
  val allMessages: RxElement[String] = textArea().run(Chat.allMessages.map(msgLine))

  publish(allMessages, username, msg)

  def render = bind("chat", "messages" -> allMessages.ui, "username" -> username.ui,  "msg" -> msg.ui)

}