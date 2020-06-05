package ca.vgorcinschi.qandauser.impl.entities

import ca.vgorcinschi.qandauser.impl.commands.UserCommand
import ca.vgorcinschi.qandauser.impl.events.UserEvent
import ca.vgorcinschi.qandauser.impl.states.{LoggedOutState, UserState}
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity

final class User extends PersistentEntity{
  override type Command = UserCommand
  override type Event = UserEvent
  override type State = UserState

  override def initialState: UserState = LoggedOutState

  override def behavior: Behavior = {
    // event and command messages handling go here
    Actions()
  }
}
