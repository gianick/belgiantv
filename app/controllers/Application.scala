package controllers

import _root_.models.helper.BroadcastInfo
import akka.actor.ActorSystem
import models.{Broadcast, BroadcastRepository, Movie, MovieRepository}
import play.api._
import play.api.mvc._
import play.api.libs.concurrent.Execution.Implicits._
import services.Mailer
import services.actors.{Master, Start, StartTomatoes}
import org.joda.time.Interval
import org.joda.time.DateTime
import org.joda.time.DateMidnight
import play.modules.reactivemongo.{MongoController, ReactiveMongoApi, ReactiveMongoComponents}

import scala.concurrent.Future

class Application(
  actorSystem: ActorSystem,
  val reactiveMongoApi: ReactiveMongoApi,
  broadcastRepository: BroadcastRepository,
  movieRepository: MovieRepository,
  mailer: Mailer,
  webJarAssets: WebJarAssets) extends Controller with MongoController with ReactiveMongoComponents {

  Logger.info("Scheduling actor trigger")
  // TODO better location for the actor?
  // TODO create an actor module that is enabled in the application.conf
  val masterActorRef = actorSystem.actorOf(Master.props(broadcastRepository, movieRepository, mailer), name = "masterActor")
  //Akka.system.scheduler.schedule(0 seconds, 12 hours, Application.masterActorRef, Start)

  private implicit val wjAssets = webJarAssets

  def index = Action.async{ implicit request =>

    // anything that has started more than an hour ago is not interesting
    val start = new DateTime().minusHours(1)
    // for seven days in the future (midnight)
    val end = new DateMidnight().plusDays(7)
    val interval = new Interval(start, end)

    for{
      broadcasts <- broadcastRepository.findByInterval(interval)
      infos <- Future.traverse(broadcasts)(broadcast => linkWithMovie(broadcast))
    }yield{
      val sorted = infos.sortWith(BroadcastInfo.scoreSorter)
      Ok(views.html.index(sorted))
    }
  }


  def linkWithMovie(broadcast: Broadcast): Future[BroadcastInfo] = {
    implicit val reader = Movie.movieFormat//MovieBSONReader

    broadcast.imdbId match {
      case Some(imdbId) => movieRepository.findByImdbId(imdbId).map(BroadcastInfo(broadcast, _))
      case None => Future.successful(BroadcastInfo(broadcast, None))
    }
  }

  def scan = Action {
    masterActorRef ! Start
    Redirect(routes.Application.index())
      .flashing("message" -> "Started database update...")
  }

  def tomatoes = Action {
    masterActorRef ! StartTomatoes
    Redirect(routes.Application.index())
      .flashing("message" -> "Started tomatoes update...")
  }
  


}