package ca.vgorcinschi.qandauserstream.impl

import com.lightbend.lagom.scaladsl.api.ServiceCall
import ca.vgorcinschi.qandauserstream.api.UserStreamService
import ca.vgorcinschi.qandauser.api.UserService

import scala.concurrent.Future

/**
  * Implementation of the UserStreamService.
  */
class UserStreamServiceImpl(qandauserService: UserService) extends UserStreamService {
  def stream = ServiceCall { hellos =>
    Future.successful(hellos.mapAsync(8)(qandauserService.hello(_).invoke()))
  }
}
