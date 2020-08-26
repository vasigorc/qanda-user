package ca.vgorcinschi.qandauser.impl.commands

import ca.vgorcinschi.qandauser.api.CredentialsPayload
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity.ReplyType
import com.lightbend.lagom.scaladsl.playjson.JsonSerializer
import play.api.libs.json.Json

sealed trait UserCommand

object UserCommand {

  val serializers = Vector(
    JsonSerializer(Json.format[LogInCommand]),
    JsonSerializer(Json.format[LoginCommandDone])
  )
}

final case class LogInCommand(content: CredentialsPayload) extends UserCommand with ReplyType[LoginCommandDone]
case object LogOutCommand extends UserCommand with ReplyType[LoginCommandDone]

final case class LoginCommandDone(userUuid: String)