package webcrawler

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.event.LoggingReceive
import scala.concurrent.forkjoin.ThreadLocalRandom
import scala.concurrent.Future
import play.api.libs.ws._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.Play.current
import akka.pattern.ask
import scala.concurrent.Await
import scala.concurrent.duration._



object LinkCollector {
  
  def collectLinks(url: String): Future[Set[String]] = {
    val request = WS.url(url)
    val response = request.get
    response.map {
      response=>
      parseBody(response.body)    
    }
    
  }
  
  def parseBody(body: String): Set[String] = {
    val A_TAG = "(?i)<a ([^>]+)>.+?</a>".r
    val HREF_ATTR = """\s*(?i)href\s*=\s*(?:"([^"]*)"|'([^']*)'|([^'">\s]+))\s*""".r
    val results =   for {
        anchor <- A_TAG.findAllMatchIn(body)
        HREF_ATTR(dquot, quot, bare) <- anchor.subgroups
      }yield  {if (dquot != null) dquot
        else if (quot != null) quot
        else bare}
        results.toSet
    }
  }
case class CrawlURL(url: String)

class WebCrawler extends Actor with ActorLogging {
  
  
  override def preStart = {
    
  }
  
   def receive = LoggingReceive{
      case CrawlURL(url) =>
        LinkCollector.collectLinks(url).map(sender ! _)
    }
}