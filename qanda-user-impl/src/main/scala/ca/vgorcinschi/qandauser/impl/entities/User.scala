package ca.vgorcinschi.qandauser.impl.entities

import ca.vgorcinschi.qandauser.impl.commands.{LoginCommand, LoginCommandDone, UserCommand}
import ca.vgorcinschi.qandauser.impl.events.UserEvent
import ca.vgorcinschi.qandauser.impl.states.{LoggedOutState, UserState}
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity
import org.mindrot.jbcrypt.BCrypt

final class User extends PersistentEntity{
  override type Command = UserCommand
  override type Event = UserEvent
  override type State = UserState

  override def initialState: UserState = LoggedOutState

  // business fields start -->
  private val maybeHashedPassword: Option[String] = None
  // <-- business fields end
  override def behavior: Behavior = {
    // event and command messages handling go here
    Actions()
      .onCommand[LoginCommand, LoginCommandDone] {
        case (LoginCommand(credentialsPayload), ctx, state) =>
          if (!BCrypt.checkpw(credentialsPayload.password, maybeHashedPassword.get)) {
            ???
          }
      }
  }
}
