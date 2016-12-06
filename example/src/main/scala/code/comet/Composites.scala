package code.comet

import com.casualmiracles.rxlift.Components._
import com.casualmiracles.rxlift.RxCometActor
import com.casualmiracles.rxlift.RxComponent
import rx.lang.scala.Subject
import rx.lang.scala.subjects.BehaviorSubject
import scalaz.Endo

import scalaz.Lens

case class Person(firstName: String, lastName: String)

object PersonComponent {

  def apply(): RxComponent[Person, Endo[Person]] = {
    val fnLens = Lens.lensu[Person, String]((p, fn) ⇒ p.copy(firstName = fn), (_: Person).firstName)
    val lnLens = Lens.lensu[Person, String]((p, ln) ⇒ p.copy(lastName = ln), (_: Person).lastName)

    val fn: RxComponent[Person, Endo[Person]] =
      focus(text(), fnLens).mapUI(ui ⇒ <span>First Name&nbsp;</span> ++ ui)

    val ln: RxComponent[Person, Endo[Person]] =
      focus(text(), lnLens).mapUI(ui ⇒ <span>Last Name&nbsp;</span> ++ ui)

    fn + ln
  }
}

class Composites extends RxCometActor {

  // In practice this will be a filtered stream
  val person: Subject[Person] = BehaviorSubject[Person](Person("", ""))

  // construct our UI component from the stream
  val pc = PersonComponent().consume(person)

  // for demo purposes we will apply this stream to the original person observable and send it back to the UI
  // In a real system you might get the person from a database, modify it with the Endo and save it.
  val newPerson = person.distinctUntilChanged.combineLatest(pc.values).map {
    case (old, update) ⇒
      val n = update(old)
      println(s"$old -> $n")
      n
  }.distinctUntilChanged.map(person.onNext(_))

  // manage the subscription to the newPerson stream
  handleSubscription(newPerson)

  def render = pc.ui
}
