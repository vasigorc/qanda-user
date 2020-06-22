package ca.vgorcinschi.qandauser.impl.entities

import akka.actor.typed.Behavior
import akka.persistence.typed.PersistenceId
import akka.persistence.typed.scaladsl.EventSourcedBehavior
import ca.vgorcinschi.qandauser.impl.commands.{LoginCommand, LoginCommandDone, UserCommand}
import ca.vgorcinschi.qandauser.impl.events.UserEvent
import ca.vgorcinschi.qandauser.impl.states.{LoggedInState, LoggedOutState, UserState}
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity
import org.mindrot.jbcrypt.BCrypt

final class User(userUuid: String, maybeHashedPassword: Option[String], username: String, persistenceId: PersistenceId) extends PersistentEntity {
  override type Command = UserCommand
  override type Event = UserEvent
  override type State = UserState

  override def initialState: UserState = LoggedOutState(userUuid)

  override def behavior: Behavior = {
    case LoggedOutState(_) => initial
    case LoggedInState(_, _) => loggedIn
  }

  private val initial: Actions = {
    // event and command messages handling go here
    Actions()
      .onCommand[LoginCommand, LoginCommandDone] {
        case (LoginCommand(credentialsPayload), ctx, state) =>
          if (!BCrypt.checkpw(credentialsPayload.password, maybeHashedPassword.get)) {
            ???
          }
      }
  }

  private val loggedIn: Actions = Actions()
    .onCommand([LoginCommand, ])
    .onEvent {
      case (e, state) => state.applyEvent(e)
    }
}

object User {
  def apply(userUuid: String, persistenceId: PersistenceId): Behavior[UserCommand] = {
    EventSourcedBehavior.withEnforcedReplies(persistenceId, LoggedOutState)
  }
}
