package code.snippet

import java.util.UUID

object MakeChat {
  def render =
        <lift:comet type="Chat" name={UUID.randomUUID().toString}>
          <form class="lift:form.ajax">
            <chat:messages>no messages :-(</chat:messages>
            <br/>
            Your name!&nbsp;<chat:username>no name :-(</chat:username>
            Speak!&nbsp;<chat:msg>no messages :-(</chat:msg>
          </form>
        </lift:comet>
}