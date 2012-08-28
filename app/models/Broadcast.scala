package models

import scala.collection.JavaConversions._
import play.api.Play.current
import play.modules.mongodb.jackson.MongoDB
import net.vz.mongodb.jackson.{Id, ObjectId}
import org.codehaus.jackson.annotate.JsonProperty
import reflect.BeanProperty
import com.mongodb.DBObject
import play.api.Logger
import org.joda.time.DateMidnight
import org.joda.time.DateTime
import net.vz.mongodb.jackson.DBUpdate
import org.joda.time.format.DateTimeFormat
import org.joda.time.DateTimeZone
import org.joda.time.Interval
import controllers.Application

class Broadcast(
    @ObjectId @Id val id: String,
    @BeanProperty @JsonProperty("name") val name: String,
    @BeanProperty @JsonProperty("channel") val channel: String,
    @BeanProperty @JsonProperty("datetime") val datetime: DateTime,
    @BeanProperty @JsonProperty("year") val year: Option[Int] = None,
    @BeanProperty @JsonProperty("humoId") val humoId: Option[String] = None,
    @BeanProperty @JsonProperty("humoUrl") val humoUrl: Option[String] = None,
    @BeanProperty @JsonProperty("yeloId") val yeloId: Option[String] = None,
    @BeanProperty @JsonProperty("yeloUrl") val yeloUrl: Option[String] = None,
    @BeanProperty @JsonProperty("belgacomId") val belgacomId: Option[String] = None,
    @BeanProperty @JsonProperty("belgacomUrl") val belgacomUrl: Option[String] = None,
    @BeanProperty @JsonProperty("imdbId") val imdbId: Option[String] = None,
    @BeanProperty @JsonProperty("tomatoesId") val tomatoesId: Option[String] = None,
    @BeanProperty @JsonProperty("tmdbId") val tmdbId: Option[String] = None,
    @BeanProperty @JsonProperty("tmdbImg") val tmdbImg: Option[String] = None
    ) {
  @ObjectId
  @Id
  def getId = id;
  
  def humanDate() = {
    val format = DateTimeFormat.forPattern("dd MMM HH:mm")
    format.print(datetime.withZone(Application.timezone))
  }
  
  def imdbUrl = imdbId.map("http://www.imdb.com/title/%s".format(_))
  def tmdbUrl = tmdbId.map("http://www.themoviedb.org/movie/%s".format(_))
  def tomatoesUrl = tomatoesId.map("http://www.rottentomatoes.com/m/%s".format(_))
  
  override def toString() = name + " " + channel + "@" + datetime 
}

object Broadcast {
  
  private val logger = Logger("application.db")
  
  private lazy val db = MongoDB.collection("broadcasts", classOf[Broadcast], classOf[String])

  def create(broadcast: Broadcast) = {
    val result = db.save(broadcast);
    val id = result.getSavedId();
    logger.debug("saved " + broadcast + " with id " + id)
    db.findOneById(id)
  }
  
  def update(broadcast: Broadcast) {
    val result = db.save(broadcast);
    val id = result.getSavedId();
    logger.debug("saved " + broadcast + " with id " + id)
  }
  
  def findAll() = { db.find().toArray.toList }
  
  def findByInterval(interval:Interval) = {
    db.find().greaterThan("datetime", interval.getStart).lessThan("datetime", interval.getEnd).toArray.toList
  }

  def findById(id: String) = Option(db.findOneById(id))
  
  def findByDateTimeAndChannel(datetime: DateTime, channel: String) = db.find().is("datetime", datetime).is("channel", channel).headOption

  def setYelo(b:Broadcast, yeloId:String, yeloUrl:String) {
    logger.debug("Saving Yelo link for " + b.name + " with id " + b.id)
    db.updateById(b.id, DBUpdate.set("yeloId", yeloId).set("yeloUrl", yeloUrl))
  }
  
  def setBelgacom(b:Broadcast, belgacomId:String, belgacomUrl:String) {
    logger.debug("Saving Belgacom link for " + b.name + " with id " + b.id)
    db.updateById(b.id, DBUpdate.set("belgacomId", belgacomId).set("belgacomUrl", belgacomUrl))
  }
  
  def setImdb(b:Broadcast, imdbId:String) {
    logger.debug("Saving IMDB link for " + b.name + " with id " + b.id)
    db.updateById(b.id, DBUpdate.set("imdbId", imdbId))
  }
  
  def setTomatoes(b:Broadcast, tomatoesId:String) {
    logger.debug("Saving Tomatoes link for " + b.name + " with id " + b.id)
    db.updateById(b.id, DBUpdate.set("tomatoesId", tomatoesId))
  }
  
  def setTmdb(b:Broadcast, tmdbId:String) {
    logger.debug("Saving TMDb link for " + b.name + " with id " + b.id)
    db.updateById(b.id, DBUpdate.set("tmdbId", tmdbId))
  }
  
  def setTmdbImg(b:Broadcast, tmdbImg:String) {
    logger.debug("Saving TMDb img for " + b.name + " with id " + b.id)
    db.updateById(b.id, DBUpdate.set("tmdbImg", tmdbImg))
  }
}