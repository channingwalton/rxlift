package code.snippet

import java.util.UUID

object MakeChat {
  def render =
    <lift:comet type="Chat" name={UUID.randomUUID().toString}>
      Your name!
      &nbsp; <chat:username>no name :-(</chat:username>
      Speak!
      &nbsp; <chat:msg>no messages :-(</chat:msg>
      <pre>
        <chat:messages>no messages :-(</chat:messages>
      </pre>
      <br/>
    </lift:comet>
}