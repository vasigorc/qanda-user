package ca.vgorcinschi.qandauser.impl.entities

import ca.vgorcinschi.qandauser.impl.commands.{LogOutCommand, LogInCommand, LoginCommandDone, UserCommand}
import ca.vgorcinschi.qandauser.impl.events.{LoggedIn, LoggedOut, UserEvent}
import ca.vgorcinschi.qandauser.impl.states.UserState
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity
import org.mindrot.jbcrypt.BCrypt

final class UserEntity extends PersistentEntity {
  override type Command = UserCommand
  override type Event = UserEvent
  override type State = UserState

  override def initialState: UserState = UserState.empty

  override def behavior: Behavior = {
    case UserState.empty => initial
    case state if state.isLoggedIn => loggedIn
    case state if !state.isLoggedIn => loggedOut
  }

  private val initial: Actions = {
    // event and command messages handling go here
    Actions()
      .onCommand[LogInCommand, LoginCommandDone] {
        case (LogInCommand(credentialsPayload), ctx, state) =>
          if (!BCrypt.checkpw(credentialsPayload.password, state.hashedPassword)) {
            ctx.invalidCommand("Invalid password provided!")
            ctx.done
          } else {
            ctx.thenPersist(state.applyEvent(LoggedIn)) { _ =>
              ctx.reply(LoginCommandDone(state.userUuid))
            }
          }
      }
  }

  private val loggedIn: Actions = {
    Actions()
      .onCommand[LogInCommand, LoginCommandDone] {
        case (LogOutCommand, ctx, state) =>
          ctx.thenPersist(state.applyEvent(LoggedOut)) { _ =>
            ctx.reply(LoginCommandDone(state.userUuid))
          }
      }
  }

  private val loggedOut: Actions = initial

}
