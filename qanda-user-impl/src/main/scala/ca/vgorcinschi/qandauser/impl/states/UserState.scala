package ca.vgorcinschi.qandauser.impl.states

import akka.cluster.sharding.typed.scaladsl.EntityTypeKey
import ca.vgorcinschi.qandauser.impl.UserState
import ca.vgorcinschi.qandauser.impl.commands.UserCommand
import ca.vgorcinschi.qandauser.impl.events.{LoggedIn, LoggedOut, UserEvent}
import play.api.libs.json.{Format, Json}

final case class UserState(userUuid: String, hashedPassword: String, username: String, isLoggedIn: Boolean) {
  def applyEvent(userEvent: UserEvent) = userEvent match {
    case LoggedIn => this.copy(isLoggedIn = true)
    case LoggedOut => this.copy(isLoggedIn = false)
  }
}

object UserState {
  val empty: UserState = UserState(null.asInstanceOf[String], null.asInstanceOf[String], null.asInstanceOf[String], isLoggedIn = false)

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