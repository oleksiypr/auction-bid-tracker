package op.assessment.so1

import op.assessment.so1.BidsRepository.Bid
import scala.concurrent.Future

object BidsRepository {
  case class Bid(
    player: String,
    item: String,
    value: Int
  )
}

class BidsRepository {
  def add(bid: Bid): Future[Unit] = ???
  def getWinner(item: String): Future[Option[Bid]] = ???
  def all(item: String): Future[List[Bid]] = ???
}
