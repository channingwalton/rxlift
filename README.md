# rxlift

[![Join the chat at https://gitter.im/channingwalton/rxlift](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/channingwalton/rxlift?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
An experimental reactive library for lift web based on [RxScala](https://github.com/ReactiveX/RxScala).

The core idea is to treat UI components as having three components:
1. a UI component, e.g. an *input* element
2. an Observable the UI component observes and maps to the UI, e.g. an Observable[String]
3. an Observable of values produced by the component, e.g. an Observable[String]

This idea is modeled by [RxComponent](core/src/main/scala/com/casualmiracles/rxlift/model.scala), which produces
[RxElements](core/src/main/scala/com/casualmiracles/rxlift/model.scala). RxComponents are factories that when
given an Observable, return an RxElement that can be rendered in a browser.

This project uses Liftweb to render these RxElements. Have a look at the examples subproject for a demo.

## Examples

### Clock
The src is [here](example/src/main/scala/code/comet/Clock.scala)

```scala
class Clock extends RxCometActor {

  override def defaultPrefix = Full("clk")

  // An Observable generating a string containing the time every 1 second
  val ticker: Observable[String] = Observable.interval(Duration(1, TimeUnit.SECONDS)).map(_ ⇒ new Date().toString)

  // construct a label with the ticker
  val timeLabel: RxElement[String] = label.consume(ticker)

  // send the JsCmds emitted by the label to the actor to send to the UI
  publish(timeLabel)

  // initial render uses the label's ui
  def render = "#time" #> timeLabel.ui
}
```
Rendered with
```html
<lift:comet type="Clock">
  <p>Current Time: <span id="time">Missing Clock</span></p>
</lift:comet>
```