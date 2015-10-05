package models

import javax.inject.Inject

import play.api.Logger
import play.api.libs.json._
import play.modules.reactivemongo.ReactiveMongoApi
import play.modules.reactivemongo.json.collection.JSONCollection

import reactivemongo.bson.BSONObjectID
import services.Mailer

import scala.concurrent.Future


class MovieRepository @Inject() (val mailer: Mailer, reactiveMongoApi: ReactiveMongoApi) extends MongoSupport {
  // TODO inject MongoSupport to get rid of the mailer dependency?

  import play.api.libs.concurrent.Execution.Implicits.defaultContext

  import play.modules.reactivemongo.json._
  import play.modules.reactivemongo.json.collection._
  import play.modules.reactivemongo.json._, ImplicitBSONHandlers._

  // TODO remove, somehow I still need this despite of
  // https://groups.google.com/forum/#!topic/reactivemongo/9BGeLw13twk
  implicit val identityWriter = JSONSerializationPack.IdentityWriter

  protected override val logger = Logger("application.db")

  private lazy val movieCollection:JSONCollection = reactiveMongoApi.db.collection[JSONCollection]("movies")

  def create(movie: Movie) = {
    val id = BSONObjectID.generate
    val withId = movie.copy(id = Some(id))
    movieCollection.insert(withId)
      .onComplete(le => mongoLogger(le, "saved movie " + withId + " with id " + id))
    withId
  }


  def find(name:String, year:Option[Int] = None) = {
    year match {
      case None => findByName(name)
      case Some(y) => findByNameAndYear(name, y)
    }
  }

  def findByName(name: String): Future[Option[Movie]] = {
    movieCollection.find(
      Json.obj("name" -> name)
    ).cursor[Movie]().headOption
  }

  def findByImdbId(imdbId: String): Future[Option[Movie]] = {
    movieCollection.find(
      Json.obj("imdbId" -> imdbId)
    ).cursor[Movie]().headOption
  }

  def findByNameAndYear(name: String, year:Int): Future[Option[Movie]] = {
    movieCollection.find(
      Json.obj("name" -> name, "year" -> year)
    ).cursor[Movie]().headOption
  }

  private def ratingToDouble(rating:String) = try{
    rating.toDouble
  } catch {
    case e : NumberFormatException => 0.0
  }

}