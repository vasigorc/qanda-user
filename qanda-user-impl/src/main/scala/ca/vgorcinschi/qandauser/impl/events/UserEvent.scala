package ca.vgorcinschi.qandauser.impl.events

import com.lightbend.lagom.scaladsl.persistence.{AggregateEvent, AggregateEventShards, AggregateEventTag}

sealed trait UserEvent extends AggregateEvent[UserEvent] {
  override def aggregateTag: AggregateEventShards[UserEvent] = UserEvent.Tag
}

  /**
   * If you expect hundreds or thousands of events per second,
   * then you may want to shard your read-side event processing load.
   * [[AggregateEventShards]] tells lagom to shard the tag used
   * based on the entity's *persistence ID*. It's important to ensure
   * all events for the same entity end up in the same tag (=> same shard),
   * else event processing may be out of order, since the read side nodes
   * will consume the event streams for their tags at different paces.
   */
object UserEvent {
    /**
     * When you shard events, you need to decide up front how many shards
     * you want to use. The more shards, the more you can scale your
     * services horizontally across many nodes. However each shard increases
     * # of read side processors that query the DB for new events. It's
     * very difficult to change the number of shards without compromising
     * the ordering of the events within entity.
     */
  val NumberOfShards = 10
  val Tag: AggregateEventShards[UserEvent] = AggregateEventTag.sharded[UserEvent](NumberOfShards)
}

final case class LoggedIn(userUuid: String) extends UserEvent
final case class LoggedOut(userUuid: String) extends UserEvent