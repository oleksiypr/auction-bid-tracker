package op.assessment.so1

import op.assessment.so1.BidsRepository.{Bid, Item, Player}
import scala.collection.concurrent.TrieMap
import scala.concurrent.{ExecutionContext, Future}

object BidsRepository {

  case class Item(item: String)
  case class Player(name: String)

  case class Bid(
    player: String,
    item: String,
    value: Int
  )
}

trait BidsRepository {

  def add(bid: Bid): Future[Unit]

  /**
    * Get a winner.
    * @param item an item a winner to be found for
    * @return bid won
    */
  def get(item: Item): Future[Option[Bid]]
  /**
    * Get all bids for an item.
    * @param item an item to be searched by
    * @return bids for an item
    */
  def all(item: Item): Future[List[Bid]]

  /**
    * Get all bids for a player.
    * @param player a player to be searched by
    * @return bids for a player
    */
  def all(player: Player): Future[List[Bid]]
}

class NaiveBidsRepository(implicit ec: ExecutionContext) extends BidsRepository {

  private[this] val bidsByItem = TrieMap.empty[Item, List[Bid]].withDefaultValue(Nil)
  private[this] val bidsByPlayer = TrieMap.empty[Player, List[Bid]].withDefaultValue(Nil)

  def add(bid: Bid): Future[Unit] = Future {
    val item = Item(bid.item)
    val player = Player(bid.player)

    bidsByItem += (item -> (bid :: bidsByItem(item)))
    bidsByPlayer += (player -> (bid :: bidsByPlayer(player)))
  }

  /**
    * Get a winner.
    * @param item an item a winner to be found for
    * @return bid won
    */
  def get(item: Item): Future[Option[Bid]] = Future {
    bidsByItem.get(item).map(_.maxBy(_.value))
  }

  /**
    * Get all bids for an item.
    * @param item an item to be searched by
    * @return bids for an item
    */
  def all(item: Item): Future[List[Bid]] = Future {
    bidsByItem(item)
  }

  /**
    * Get all bids for a player.
    * @param player a player to be searched by
    * @return bids for a player
    */
  def all(player: Player): Future[List[Bid]] = Future {
    bidsByPlayer(player)
  }
}
