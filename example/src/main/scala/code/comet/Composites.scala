package code.comet

import com.casualmiracles.rxlift.Components._
import com.casualmiracles.rxlift.RxCometActor
import com.casualmiracles.rxlift.RxComponent
import rx.lang.scala.Observable
import rx.lang.scala.Subject
import rx.lang.scala.subjects.BehaviorSubject
import scalaz.Endo

import scalaz.Lens

case class Person(firstName: String, lastName: String)

object PersonComponent {

  def apply(): RxComponent[Person, Endo[Person]] = {
    val fnLens = Lens.lensu[Person, String]((p, fn) ⇒ p.copy(firstName = fn), (_: Person).firstName)
    val lnLens = Lens.lensu[Person, String]((p, ln) ⇒ p.copy(lastName = ln), (_: Person).lastName)

    val fn = focus(text(), fnLens).mapUI(ui ⇒ <span>First Name</span> ++ ui)
    val ln = focus(text(), lnLens).mapUI(ui ⇒ <span>Last Name</span> ++ ui)

    fn + ln
  }
}

class Composites extends RxCometActor {

  import PersonComponent._

  val person: Subject[Person] = BehaviorSubject[Person](Person("", ""))

  val pc = PersonComponent().consume(person)

  val updates: Observable[Endo[Person]] = pc.values

  // for demo purposes we will apply this stream to the original person observable and send it back to the UI
  // In a real system you might get the person from a database, modify it with the Endo and save it.
  val newPerson = person.distinctUntilChanged.combineLatest(updates).map {
    case (old, update) ⇒
      val updated = update(old)
     // person.onNext(updated)
      updated
  }

  // lets subscribe to the newPerson and print it
  handleSubscription(newPerson.map(println(_)))

  def render = pc.ui
}
