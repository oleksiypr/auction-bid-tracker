package op.assessment.so1

import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import op.assessment.so1.BidRoutes.Ammount
import op.assessment.so1.BidRoutesSpec.FakeBidsRepo
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
}

class BidRoutesSpec extends
  WordSpec with Matchers with ScalaFutures with ScalatestRouteTest with BidRoutes {

  import HttpMethods._

  lazy val routes = bidRoutes

  override val bidsRepo: BidsRepository = new FakeBidsRepo

  "BidRoutes" should {
    "return 204 No Content" in {
      val amaunt = Ammount(100)
      val amauntEntity = Marshal(amaunt).to[MessageEntity].futureValue
      val request = Put(
          "/bids/items/item-1/players/joe"
        ).withEntity(amauntEntity)

      request ~> routes ~> check {
        status should ===(StatusCodes.NoContent)
      }
    }
  }
}
