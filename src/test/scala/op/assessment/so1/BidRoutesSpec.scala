package op.assessment.so1

import akka.actor.ActorSystem
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import op.assessment.so1.BidRoutes.Ammount
import op.assessment.so1.BidRoutesSpec.{BidsNotFoundRepo, FailBidsRepo, FakeBidsRepo}
import org.scalatest.{Matchers, WordSpec}
import org.scalatest.concurrent.ScalaFutures
import scala.concurrent.Future

object BidRoutesSpec {
  import BidsRepository._

  class FakeBidsRepo extends BidsRepository {
    override def add(bid: Bid): Future[Unit] = {
      Future.unit
    }

    override def get(item: Item): Future[Option[Bid]] = {
      Future.successful(Some(Bid("Joe", "item-1", 100)))
    }

    override def all(item: Item): Future[List[Bid]] = {
      Future.successful(List(Bid("Joe", "item-1", 100)))
    }

    override def all(player: Player): Future[List[Bid]] =  {
      Future.successful(List(Bid("Joe", "item-1", 100)))
    }
  }

  class BidsNotFoundRepo extends BidsRepository {
    override def get(item: Item): Future[Option[Bid]] = {
      Future.successful(None)
    }

    override def all(item: Item): Future[List[Bid]] = {
      Future.successful(Nil)
    }

    override def all(player: Player): Future[List[Bid]] = {
      Future.successful(Nil)
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

  "BidRoutes PUT" should {
    "return 204 No Content" in new BidRoutes {
      implicit val system: ActorSystem = self.system

      lazy val routes: Route = bidRoutes
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

      lazy val routes: Route = bidRoutes
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

  "BidRoutes GET winner" should {
    "return 200 Ok" in new BidRoutes {
      implicit val system: ActorSystem = self.system

      lazy val routes: Route = bidRoutes
      override val bidsRepo: BidsRepository = new FakeBidsRepo

      val request: HttpRequest = Get("/bids/items/item-1")

      request ~> routes ~> check {
        status should ===(StatusCodes.OK)
        contentType should === (ContentTypes.`application/json`)
        entityAs[String] should ===(
          """{"player":"Joe","item":"item-1","value":100}""".stripMargin)
      }
    }
    "return 404 Not Found" in new BidRoutes {
      implicit val system: ActorSystem = self.system

      lazy val routes: Route = bidRoutes
      override val bidsRepo: BidsRepository = new BidsNotFoundRepo

      val request: HttpRequest = Get("/bids/items/item-1")

      request ~> routes ~> check {
        status should ===(StatusCodes.NotFound)
      }
    }
  }

  "BidRoutes GET bids for an item" should {
    "return 200 Ok" in new BidRoutes {
      implicit val system: ActorSystem = self.system

      lazy val routes: Route = bidRoutes
      override val bidsRepo: BidsRepository = new FakeBidsRepo

      val request: HttpRequest = Get("/bids?item=item-1")

      request ~> routes ~> check {
        status should ===(StatusCodes.OK)
        contentType should === (ContentTypes.`application/json`)
        entityAs[String] should ===(
          """{"values":[{"player":"Joe","item":"item-1","value":100}]}""".stripMargin)
      }
    }
    "return 404 Not Found" in new BidRoutes {
      implicit val system: ActorSystem = self.system

      lazy val routes: Route = bidRoutes
      override val bidsRepo: BidsRepository = new BidsNotFoundRepo

      val request: HttpRequest = Get("/bids?item=item-1")

      request ~> routes ~> check {
        status should ===(StatusCodes.NotFound)
      }
    }
  }

  "BidRoutes GET bids made by user" should {
    "return 200 Ok" in new BidRoutes {
      implicit val system: ActorSystem = self.system

      lazy val routes: Route = bidRoutes
      override val bidsRepo: BidsRepository = new FakeBidsRepo

      val request: HttpRequest = Get("/bids/items?player=joe")

      request ~> routes ~> check {
        status should ===(StatusCodes.OK)
        contentType should === (ContentTypes.`application/json`)
        entityAs[String] should ===(
          """{"values":[{"player":"Joe","item":"item-1","value":100}]}""".stripMargin)
      }
    }
    "return 404 Not Found" in new BidRoutes {
      implicit val system: ActorSystem = self.system

      lazy val routes: Route = bidRoutes
      override val bidsRepo: BidsRepository = new BidsNotFoundRepo

      val request: HttpRequest = Get("/bids/items?player=joe")

      request ~> routes ~> check {
        status should ===(StatusCodes.NotFound)
      }
    }
  }
}
