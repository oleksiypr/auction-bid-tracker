package op.assessment.so1

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import op.assessment.so1.BidRoutes.{Ammount, Bids, Fail}
import op.assessment.so1.NaiveBidsRepository.Bid
import spray.json.DefaultJsonProtocol

trait JsonSupport extends SprayJsonSupport {

  import DefaultJsonProtocol._

  implicit val ammountFormat = jsonFormat1(Ammount)
  implicit val failFormat = jsonFormat1(Fail)
  implicit val bidFormat = jsonFormat3(Bid)
  implicit val bidsFormat = jsonFormat1(Bids)
}

