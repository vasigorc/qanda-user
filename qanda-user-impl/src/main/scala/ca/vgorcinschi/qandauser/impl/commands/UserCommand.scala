package ca.vgorcinschi.qandauser.impl.commands

import ca.vgorcinschi.qandauser.api.CredentialsPayload
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity.ReplyType
import com.lightbend.lagom.scaladsl.playjson.JsonSerializer
import play.api.libs.json.Json

sealed trait UserCommand

object UserCommand {

  val serializers = Vector(
    JsonSerializer(Json.format[Login]),
    JsonSerializer(Json.format[LoginDone])
  )
}

final case class Login(content: CredentialsPayload) extends UserCommand with ReplyType[LoginDone]

final case class LoginDone(userUuid: String)