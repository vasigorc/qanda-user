package ca.vgorcinschi.qandauserstream.impl

import com.lightbend.lagom.scaladsl.api.ServiceLocator.NoServiceLocator
import com.lightbend.lagom.scaladsl.server._
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import play.api.libs.ws.ahc.AhcWSComponents
import ca.vgorcinschi.qandauserstream.api.UserStreamService
import ca.vgorcinschi.qandauser.api.UserService
import com.softwaremill.macwire._

class UserStreamLoader extends LagomApplicationLoader {

  override def load(context: LagomApplicationContext): LagomApplication =
    new UserStreamApplication(context) {
      override def serviceLocator: NoServiceLocator.type = NoServiceLocator
    }

  override def loadDevMode(context: LagomApplicationContext): LagomApplication =
    new UserStreamApplication(context) with LagomDevModeComponents

  override def describeService = Some(readDescriptor[UserStreamService])
}

abstract class UserStreamApplication(context: LagomApplicationContext)
  extends LagomApplication(context)
    with AhcWSComponents {

  // Bind the service that this server provides
  override lazy val lagomServer: LagomServer = serverFor[UserStreamService](wire[UserStreamServiceImpl])

  // Bind the UserService client
  lazy val qandauserService: UserService = serviceClient.implement[UserService]
}
