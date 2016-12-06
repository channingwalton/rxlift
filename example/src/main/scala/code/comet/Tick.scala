package code.comet

import net.liftweb.http.CometActor
import net.liftweb.http.js.JsCmds.SetHtml
import net.liftweb.util.Schedule
import net.liftweb.util.Helpers._

import scala.xml.Text

/*
Put the following anywhere in a webpage to get a tick

<lift:comet type="Tick"><span id="tick">Waiting for first tick</span></lift:comet>

 */
class Tick extends CometActor {

  // use this as a message to send to this actor
  object TickTock

  // schedule a message send in 1 second
  def scheduleNextTick = Schedule(() ⇒ this ! TickTock, 1 second)

  // initial render when the actor is first created for a page
  def render = {
    scheduleNextTick
    "#tick" #> <p>Waiting for first tick</p>
  }

  // receive messages and send updates to the browser using partialUpdate
  final override def lowPriority : PartialFunction[Any, Unit] = {
    case TickTock ⇒
      scheduleNextTick
      partialUpdate(SetHtml("tick", Text(System.currentTimeMillis().toString)))
  }
}