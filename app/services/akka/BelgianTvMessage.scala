package services.akka
import org.joda.time.DateMidnight
import models.Broadcast
import models.helper.TomatoesMovie
import reactivemongo.bson.BSONObjectID


sealed trait BelgianTvMessage

/**
 * Sent to Master Actor
 */
case object Start extends BelgianTvMessage

case object StartTomatoes extends BelgianTvMessage

case class LinkImdb(broadcast:Broadcast) extends BelgianTvMessage

case class LinkTmdb(broadcast:Broadcast) extends BelgianTvMessage

case class LinkTomatoes(broadcast:Broadcast) extends BelgianTvMessage

// TODO how do I avoid sending the broadcastId?
case class FetchTomatoes(title:String, year: Option[Int], broadcastId: BSONObjectID) extends BelgianTvMessage

case class FetchTomatoesResult(tomatoesMovie:TomatoesMovie, broadcastId: BSONObjectID) extends BelgianTvMessage

case class FetchHumo(day:DateMidnight) extends BelgianTvMessage

case class FetchYelo(day:DateMidnight, events: List[Broadcast]) extends BelgianTvMessage

case class FetchBelgacom(day:DateMidnight, events: List[Broadcast]) extends BelgianTvMessage
