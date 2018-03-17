package op.assessment.sn

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import scala.concurrent.{Await, ExecutionContextExecutor}
import scala.concurrent.duration.Duration

object Server extends App with BidRoutes {

  implicit val system: ActorSystem = ActorSystem("bitTrackerServer")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val ec: ExecutionContextExecutor = system.dispatcher

  val bidsRepo = new NaiveBidsRepository
  lazy val routes: Route = bidRoutes

  Http().bindAndHandle(routes, "localhost", 8080)
  println(s"Server online at http://localhost:8080/")

  Await.result(system.whenTerminated, Duration.Inf)
}

