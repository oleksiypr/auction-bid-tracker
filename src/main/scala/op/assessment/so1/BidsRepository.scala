package op.assessment.so1

import op.assessment.so1.BidsRepository.{Bid, Item, Player}
import scala.concurrent.Future

object BidsRepository {

  case class Item(item: String)
  case class Player(name: String)

  case class Bid(
    player: String,
    item: String,
    value: Int
  )
}

class BidsRepository {

  def add(bid: Bid): Future[Unit] = ???

  /**
    * Get a winner.
    * @param item an item a winner to be found for
    * @return bid won
    */
  def get(item: Item): Future[Option[Bid]] = ???

  /**
    * Get all bids for an item.
    * @param item an item to be searched by
    * @return bids for an item
    */
  def all(item: Item): Future[List[Bid]] = ???

  /**
    * Get all bids for a player.
    * @param player a player to be searched by
    * @return bids for a player
    */
  def all(player: Player): Future[List[Bid]] = ???
}
