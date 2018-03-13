package op.assessment.so1

import akka.http.scaladsl.model.{ContentTypes, HttpMethods, HttpRequest, StatusCodes}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.{Matchers, WordSpec}
import org.scalatest.concurrent.ScalaFutures

class BidRoutesSpec extends
  WordSpec with Matchers with ScalaFutures with ScalatestRouteTest with BidRoutes {

  import HttpMethods._

  lazy val routes = bidRoutes

  "BidRoutes" should {
    "return 204 No Content" in {
      val request = HttpRequest(
          method = PUT,
          uri = "/bids/items/item-1/players/joe")

      request ~> routes ~> check {
        status should ===(StatusCodes.NoContent)
      }
    }
  }
}
