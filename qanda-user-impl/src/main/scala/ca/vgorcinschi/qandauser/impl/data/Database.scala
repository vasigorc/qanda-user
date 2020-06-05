package ca.vgorcinschi.qandauser.impl.data

import akka.Done
import akka.persistence.query.Offset
import ca.vgorcinschi.qandauser.impl.events.UserEvent
import com.lightbend.lagom.scaladsl.persistence.AggregateEventTag

import scala.concurrent.Future

trait Database {
  /**
   * Create the tables needed for this read side if not already created.
   */
  def createTables(): Future[Done]

  /**
   * Load the offset of the last event processed.
   */
  def loadOffset(tag: AggregateEventTag[UserEvent]): Future[Offset]

  /**
   * Handle the post added event
   */
  def handleEvent(event: UserEvent, offset: Offset): Future[Done]
}
