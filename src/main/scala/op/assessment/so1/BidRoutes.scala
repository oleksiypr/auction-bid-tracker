package op.assessment.so1

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Route
import akka.util.Timeout
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.MethodDirectives.{ delete, get, post }
import akka.http.scaladsl.server.directives.PathDirectives.path
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import scala.concurrent.Future
import scala.concurrent.duration._

import scala.concurrent.duration._

trait BidRoutes extends JsonSupport {

  implicit def system: ActorSystem

  implicit lazy val timeout = Timeout(5.seconds)

  lazy val bidRoutes: Route =  path("bids" / "items" / Segment /  "players" / Segment) { (item, player) =>
    put { ctx =>
      ctx.complete(StatusCodes.NoContent)
    }
  }

}
