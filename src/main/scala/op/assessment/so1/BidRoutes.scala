package op.assessment.so1

import akka.actor.ActorSystem
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.MethodDirectives.get
import akka.http.scaladsl.server.directives.PathDirectives.path
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import op.assessment.so1.BidRoutes.{Ammount, Bids, Fail}
import op.assessment.so1.BidsRepository.Bid

import scala.util.{Failure, Success}

object BidRoutes {
  case class Ammount(value: Int)
  case class Bids(values: List[Bid])
  case class Fail(err: String)
}

trait BidRoutes extends JsonSupport {

  implicit def system: ActorSystem

  val bidsRepo: BidsRepository

  lazy val bidRoutes: Route =  path("bids" / "items" / Segment /  "players" / Segment) {
    (item, player) => put {
      entity(as[Ammount]) { ammount: Ammount =>
        val bidDone = bidsRepo.add(Bid(
          player, item, ammount.value
        ))

        onComplete(bidDone) {
          case Success(_) => complete(StatusCodes.NoContent)
          case Failure(err) => complete((
              StatusCodes.InternalServerError,
              Fail(err.getMessage)
            ))
        }
      }
    }
  } ~ path("bids" / "items" / Segment) { item =>
    get {
      val winner = bidsRepo.getWinner(item)
      onComplete(winner) {
        case Success(Some(bid)) => complete((StatusCodes.OK, bid))
        case Success(None) => complete(StatusCodes.NotFound)
        case Failure(err) => complete((
          StatusCodes.InternalServerError,
          Fail(err.getMessage)
        ))
      }
    }
  } ~ path("bids") {
    get {
      parameters('item) { item =>
        val bids = bidsRepo.all(item)
        onComplete(bids) {
          case Success(values) if values.nonEmpty => complete((StatusCodes.OK, Bids(values)))
          case Success(_) => complete(StatusCodes.NotFound)
          case Failure(err) => complete((
            StatusCodes.InternalServerError,
            Fail(err.getMessage)
          ))
        }
      }
    }
  }
}
