package op.assessment.so1

//#json-support
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import op.assessment.so1.BidRoutes.{Ammount, Fail}
import op.assessment.so1.BidsRepository.Bid
import op.assessment.so1.UserRegistryActor.ActionPerformed
import spray.json.DefaultJsonProtocol

trait JsonSupport extends SprayJsonSupport {

  import DefaultJsonProtocol._

  implicit val ammountFormat = jsonFormat1(Ammount)
  implicit val failFormat = jsonFormat1(Fail)
  implicit val bidFormat = jsonFormat3(Bid)

  implicit val userJsonFormat = jsonFormat3(User)
  implicit val usersJsonFormat = jsonFormat1(Users)

  implicit val actionPerformedJsonFormat = jsonFormat1(ActionPerformed)
}
//#json-support
