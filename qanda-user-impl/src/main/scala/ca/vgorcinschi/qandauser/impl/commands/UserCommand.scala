package ca.vgorcinschi.qandauser.impl.commands

import ca.vgorcinschi.qandauser.api.CredentialsPayload
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity.ReplyType
import com.lightbend.lagom.scaladsl.playjson.JsonSerializer
import play.api.libs.json.Json

sealed trait UserCommand

object UserCommand {

  val serializers = Vector(
    JsonSerializer(Json.format[LoginCommand]),
    JsonSerializer(Json.format[LoginCommandDone])
  )
}

final case class LoginCommand(content: CredentialsPayload) extends UserCommand with ReplyType[LoginCommandDone]

final case class LoginCommandDone(userUuid: String)