package controllers

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc.Action
import scala.concurrent.Future
import play.api.mvc.Controller
import akka.actor.ActorRef
import webcrawler.CrawlURL
import akka.pattern.ask
import akka.util.Timeout 
import scala.concurrent.duration._

class MainController(crawler: ActorRef) extends Controller{
  def getLinks = Action.async {
    val url ="http://www.hindustantimes.com/newdelhi"
     implicit val timeout = Timeout(10000 second) 
     val askResult:Future[Set[String]] = ask(crawler,CrawlURL(url)).mapTo[Set[String]]
     val result = askResult.map{links=>
       ("Results\n" /: links)((link,buffer) => link + s"$buffer\n")}
     result.map {Ok(_)}
  }
}