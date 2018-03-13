package op.assessment.so1

import akka.actor.ActorSystem
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import op.assessment.so1.BidRoutes.Ammount
import op.assessment.so1.BidRoutesSpec.{FailBidsRepo, FakeBidsRepo}
import org.scalatest.{Matchers, WordSpec}
import org.scalatest.concurrent.ScalaFutures

import scala.concurrent.Future

object BidRoutesSpec {
  import BidsRepository._

  class FakeBidsRepo extends BidsRepository {
    override def add(bid: Bid): Future[Unit] = {
      Future.unit
    }
  }

  class FailBidsRepo extends BidsRepository {
    override def add(bid: Bid): Future[Unit] = {
      Future.failed(new RuntimeException("failed"))
    }
  }
}

class BidRoutesSpec extends
  WordSpec with Matchers with ScalaFutures with ScalatestRouteTest { self =>

  "BidRoutes" should {
    "return 204 No Content" in new BidRoutes {
      implicit val system: ActorSystem = self.system

      lazy val routes = bidRoutes
      override val bidsRepo: BidsRepository = new FakeBidsRepo

      val request: HttpRequest = Put(
          "/bids/items/item-1/players/joe"
        ).withEntity(
          Marshal(Ammount(100)).to[MessageEntity].futureValue
        )

      request ~> routes ~> check {
        status should ===(StatusCodes.NoContent)
      }
    }
    "return 500  Internal Server Error" in new BidRoutes {
      implicit val system: ActorSystem = self.system

      lazy val routes = bidRoutes
      override val bidsRepo: BidsRepository = new FailBidsRepo

      val request: HttpRequest = Put(
          "/bids/items/item-1/players/joe"
        ).withEntity(
          Marshal(Ammount(200)).to[MessageEntity].futureValue
        )

      request ~> routes ~> check {
        status should ===(StatusCodes.InternalServerError)
        contentType should ===(ContentTypes.`application/json`)
        entityAs[String] should ===("""{"err":"failed"}""")
      }
    }
  }
}
