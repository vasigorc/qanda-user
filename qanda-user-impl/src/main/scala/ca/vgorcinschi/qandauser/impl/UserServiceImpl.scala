package ca.vgorcinschi.qandauser.impl

import akka.{Done, NotUsed}
import akka.cluster.sharding.typed.scaladsl.{ClusterSharding, EntityRef}
import akka.util.Timeout
import ca.vgorcinschi.qandauser.api
import ca.vgorcinschi.qandauser.api.{CredentialsPayload, UserService}
import ca.vgorcinschi.qandauser.impl.commands.UserCommand
import ca.vgorcinschi.qandauser.impl.entities.UserEntity
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.api.broker.Topic
import com.lightbend.lagom.scaladsl.api.transport.BadRequest
import com.lightbend.lagom.scaladsl.broker.TopicProducer
import com.lightbend.lagom.scaladsl.persistence.{EventStreamElement, PersistentEntityRegistry}

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._

/**
 * Implementation of the UserService.
 */
class UserServiceImpl(
                       clusterSharding: ClusterSharding,
                       persistentEntityRegistry: PersistentEntityRegistry
                     )(implicit ec: ExecutionContext)
  extends UserService {

  persistentEntityRegistry.register(new UserEntity)

  /**
   * Looks up the entity for the given ID.
   */
  private def entityRef(id: String): EntityRef[UserCommand] =
    clusterSharding.entityRefFor(UserState.typeKey, id)

  implicit val timeout = Timeout(5.seconds)

  override def hello(id: String): ServiceCall[NotUsed, String] = ServiceCall {
    _ =>
      // Look up the sharded entity (aka the aggregate instance) for the given ID.
      val ref = entityRef(id)

      // Ask the aggregate instance the Hello command.
      ref
        .ask[Greeting](replyTo => Hello(id, replyTo))
        .map(greeting => greeting.message)
  }

  override def useGreeting(id: String) = ServiceCall { request =>
    // Look up the sharded entity (aka the aggregate instance) for the given ID.
    val ref = entityRef(id)

    // Tell the aggregate to use the greeting message specified.
    ref
      .ask[Confirmation](
        replyTo => UseGreetingMessage(request.message, replyTo)
      )
      .map {
        case Accepted => Done
        case _ => throw BadRequest("Can't upgrade the greeting message.")
      }
  }

  override def greetingsTopic(): Topic[api.GreetingMessageChanged] =
    TopicProducer.singleStreamWithOffset { fromOffset =>
      persistentEntityRegistry
        .eventStream(OldUserEvent.Tag, fromOffset)
        .map(ev => (convertEvent(ev), ev.offset))
    }

  private def convertEvent(
                            helloEvent: EventStreamElement[OldUserEvent]
                          ): api.GreetingMessageChanged = {
    helloEvent.event match {
      case GreetingMessageChanged(msg) =>
        api.GreetingMessageChanged(helloEvent.entityId, msg)
    }
  }

  override def login(): ServiceCall[CredentialsPayload, String] = {
    credentialsPayload: CredentialsPayload =>
      Option(credentialsPayload.username)
        .fold(Future.successful("User name is required!")) {
          username =>
            val ref = persistentEntityRegistry.refFor[UserEntity](username.##.toString)
            val reply = ref.ask(commands.LogInCommand(credentialsPayload))
            reply.map(userId => userId.userUuid)
        }
  }
}
