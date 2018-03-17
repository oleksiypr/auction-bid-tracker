package op.assessment.sn

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import op.assessment.sn.BidRoutes.{Ammount, Bids, Fail}
import op.assessment.sn.BidsRepository.Bid
import spray.json.DefaultJsonProtocol

trait JsonSupport extends SprayJsonSupport {

  import DefaultJsonProtocol._

  implicit val ammountFormat = jsonFormat1(Ammount)
  implicit val failFormat = jsonFormat1(Fail)
  implicit val bidFormat = jsonFormat3(Bid)
  implicit val bidsFormat = jsonFormat1(Bids)
}

