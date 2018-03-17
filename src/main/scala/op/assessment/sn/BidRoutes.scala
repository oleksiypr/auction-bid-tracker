package op.assessment.sn

import akka.actor.ActorSystem
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.MethodDirectives.get
import akka.http.scaladsl.server.directives.PathDirectives.path
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import op.assessment.sn.BidRoutes.{Ammount, Bids, Fail}
import op.assessment.sn.BidsRepository.{Bid, Item, Player}

import scala.util.{Failure, Success}

object BidRoutes {
  case class Ammount(value: Int)
  case class Bids(values: List[Bid])
  case class Fail(err: String)
}

/**
  * Bids end-points routes.
  *
  * 1. Record a user's bid on an item:
  *   PUT /bids/items/{itemId}/players/{playerName}
  *
  * 2. Get the current winning bid for an item:
  *   GET /bids/items/{itemId}
  *
  * 3. Get all the bids for an item:
  *   GET /bids?item={itemId}
  *
  * 4. Get all the items on which a user has bid:
  *   GET /bids/items?player={playerName}
  */
trait BidRoutes extends JsonSupport {

  implicit def system: ActorSystem

  val bidsRepo: BidsRepository

  lazy val bidRoutes: Route = path("bids" / "items" / Segment / "players" / Segment) {
    (item, player) =>
      put {
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
      val winner = bidsRepo.get(Item(item))
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
        val bids = bidsRepo.all(Item(item))
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
  } ~ path("bids" / "items") {
    get {
      parameters('player) { name =>
        val bids = bidsRepo.all(Player(name))
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
