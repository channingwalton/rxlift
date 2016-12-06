package code.snippet

import java.util.UUID

object MakeChat {
  def render =
    <lift:comet type="Chat" name={UUID.randomUUID().toString}>
      Your name!
      &nbsp; <span id="username">no name :-(</span>
      Speak!
      &nbsp; <span id="msg">no messages :-(</span>
      <pre>
        <span id="messages">no messages :-(</span>
      </pre>
      <br/>
    </lift:comet>
}