package ca.vgorcinschi.qandauser.impl.states

import akka.cluster.sharding.typed.scaladsl.EntityTypeKey
import ca.vgorcinschi.qandauser.impl.UserState
import ca.vgorcinschi.qandauser.impl.commands.UserCommand
import ca.vgorcinschi.qandauser.impl.events.{LoggedIn, LoggedOut, UserEvent}
import play.api.libs.json.{Format, Json}

sealed trait UserState {
  def applyEvent(event: UserEvent): UserState
}

case class LoggedInState(userUuid: String, username: String) extends UserState {
  override def applyEvent(event: UserEvent): UserState = event match {
    case LoggedIn(userUuid, username) => throw new IllegalStateException(s"User with UUID $userUuid is already logged-in")
    case LoggedOut(userUuid) => LoggedOutState(userUuid)
  }
}

case class LoggedOutState(userUuid: String) extends UserState {
  override def applyEvent(event: UserEvent): UserState = event match {
    case LoggedIn(userUuid, username) => LoggedInState(userUuid, username)
    case LoggedOut(userUuid) => throw new IllegalStateException(s"User with UUID $userUuid is already logged-out")
  }
}

object UserState {
  val typeKey: EntityTypeKey[UserCommand] = EntityTypeKey[UserCommand]("UserAggregate")

  /**
   * Format for the hello state.
   *
   * Persisted entities get snapshotted every configured number of events. This
   * means the state gets stored to the database, so that when the aggregate gets
   * loaded, you don't need to replay all the events, just the ones since the
   * snapshot. Hence, a JSON format needs to be declared so that it can be
   * serialized and deserialized when storing to and from the database.
   */
  implicit val format: Format[UserState] = Json.format
}