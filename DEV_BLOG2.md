1. Started project. Create avl and push my first commit so I can push later on updates directly.

2.According to my designed graph, created RatingAvlTree (avl tree sorted by rating, stores (playerId, rating)); QueuedPlayer (records when a player started queuing and it's rating); MatchmakingWindow (a queuing window increase by time. once a player starts queuing, it's window is at a particular size and the window increases every 1ms); MatchmakingQueue (put players in queue(add them to the avl tree), remove players once they find a opponent(remove from the tree), search players(find in the window))

3.RatingAvlTree
problem:
can't insert 2 different players with same rating.
solve:
compare id after rating

problem solved



problem:
count in area was negative
solve:
use < for countLt, instead of <=

still exist

solve:
refresh after rotate

still exist

solve:
go to other side of child.

problem solved



problem:
size incorrect after i insert 2 same node
solve:
add another else statement to throw exception

4.QueuedPlayer
Just a constructor, didn't met any problems

5.MatchmakingWindow
problem:
window is smaller and smaller

solve:
check initialHalfWidth and growthPerMs before creating window

problem solved



problem:
can't find match after long wait

solve:
used wrong player. only use closedRatingBounds for seekers

problem solved

6.MatchMakingQueue
problem:
Map is empty but node exist in tree

solve:
only removed player but did not delete from tree. deleted after remove

problem solved



problem:
only 1 player in queue but found opponent

solve:
excludeId

problem solved



problem:
opponent count incorrect

solve:
exclude the node itself

problem solved



7.tests
moved test files into separate folder and found out that I did something dumb. I don't need a RatingAvlTree and an avlTree that super from it. removed it.
