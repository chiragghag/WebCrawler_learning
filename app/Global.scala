import play.api.{Logger, Application}
import play.api.mvc.Results._
import play.api.mvc._
import scala.concurrent.Future
import play.api.libs.concurrent.Akka
import akka.actor.ActorRef
import akka.actor.Props
import webcrawler.WebCrawler
import controllers.MainController
import play.api.GlobalSettings

object Global extends GlobalSettings {

  val log: Logger = Logger(this.getClass)
  
   var webCrawler: ActorRef = _

  override def onStart(app: Application) {
    import play.api.Play.current
    lazy val akka = Akka.system
    webCrawler = akka.actorOf(Props[WebCrawler], "webCrawler")
  }

  override def getControllerInstance[A](controllerClass: Class[A]) = {
    if (controllerClass != classOf[MainController]) {
      throw new RuntimeException("Could not create a controller of type" + controllerClass)
    }
    new MainController(webCrawler).asInstanceOf[A]
  }

  override def onHandlerNotFound(request: RequestHeader) = {
    Future.successful(NotFound("Not Found"))
  }

  override def onBadRequest(request: RequestHeader, error: String) = {
    Future.successful(BadRequest("Bad Request: " + error))
  }
}