package code.comet

import com.casualmiracles.rxlift.RxCometActor
import com.casualmiracles.rxlift.Components._

import net.liftweb.common.Full
import net.liftweb.http.RenderOut
import rx.lang.scala.Observable

class Select extends RxCometActor {

  override def defaultPrefix = Full("select")

  val sel = select((1 to 50).toList.map(i => (i.toString, i.toString)), Full(1.toString)).consume(Observable.empty)
  val out = label.consume(sel.values)

  publish(out)

  override def render: RenderOut = bind("select" -> sel.ui, "label" -> out.ui)
}
