**Auction Bid Tracker**

You have been tasked with building part of a simple online auction system which will allow
users to concurrently bid on items for sale.
Provide a bid tracker interface and concrete implementation with the following functionality:
* Record a user's bid on an item
* Get the current winning bid for an item
* Get all the bids for an item
* Get all the items on which a user has bid
* Try to store state changes in redis as events.
* Build simple REST API to manage bids.

You are not required to implement a GUI (or CLI) or persistent store (events are for reporting
only). 

You may use any appropriate libraries to help.
Test the performance of your solution. Compare to a naive implementation with
synchronized locking.