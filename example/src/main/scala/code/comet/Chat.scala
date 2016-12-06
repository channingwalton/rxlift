package code.comet

import com.casualmiracles.rxlift.Components._
import com.casualmiracles.rxlift.{RxComponent, RxCometActor, RxElement}
import net.liftweb.common.Full
import net.liftweb.http.js.JsCmds.Focus

import rx.lang.scala.{Observable, Subject}
import rx.lang.scala.subjects.BehaviorSubject

case class Message(username: String, msg: String)

object Chat {
  val send: Subject[Message] = BehaviorSubject[Message](Message("System", "Welcome"))
  val allMessages: Observable[Seq[Message]] = send.scan(Seq.empty[Message])(_ :+ _)
}

class Chat extends RxCometActor {

  override def defaultPrefix = Full("chat")

  // whose talking? There is no input to this component so its just constructed with an empty Observable
  val username: RxElement[String] = text().consume(Observable.empty)

  // the message input is an initially disabled text component, enabled by the username being set
  val usernameNonEmpty: Observable[Boolean] = username.values.map(_.trim.nonEmpty)
  val msgEditable: Observable[Boolean] = Observable.just(false).merge(usernameNonEmpty)
  val editableMsg: RxComponent[String, String] = editable(text(), msgEditable)

  // run the editableMsg with a Subject[String] which is used below to reset the input field when the user types a message
  val msgIn: Subject[String] = Subject[String]()
  val msg: RxElement[String] = editableMsg.consume(msgIn)

  // combine the output of username and msg and map it to a Message
  val messages: Observable[Message] = username.values.combineLatest(msg.values).map{ case (u, m) ⇒ Message(u, m) }

  // send the user's message to the 'chat server', blank the msg field and give it focus
  // RxCometActor will handle the Subscription management for us
  handleSubscription(messages.filter(_.msg.trim.nonEmpty).map(m ⇒ {
    Chat.send.onNext(m)
    msgIn.onNext("")
    this ! Focus(msg.id)
  }))

  // a textarea whose content is obtained from Chat.messages
  def msgLine(msgs: Seq[Message]): String = msgs.map(m ⇒ m.username + ": " + m.msg).mkString("\n")
  val allMessages: RxElement[String] = label.consume(Chat.allMessages.map(msgLine))

  // publish all the components so their output JsCmds are sent to the client
  publish(allMessages, username, msg)

  // the usual CometActor.render to bind the html for each ui component into an HTML page
  def render =
    "#messages" #> allMessages.ui &
    "#username" #> username.ui &
    "#msg" #> msg.ui

}