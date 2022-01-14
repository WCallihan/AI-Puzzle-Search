How to execute code: java Homework1.java <search_type> <input_file>

<search_type> can be one of the following:
"depth-first" - executes a depth-first search algorithm
"iterative-deepening" - executes a iterative deepening search algorithm
"astar1" - executes an A* search algorithm with heuristic 1 (the number of tiles not in their goal position)
"astar2" - executes an A* search algorithm with heuristic 2 (the sum of the distances of all the tiles away from their goal positions)

<input_file> is the path of the file containing the string of the initial input state in the form of "* 2 3 1 7 5 4 6 8" (for example).
The input file can hold multiple input states in a row as long as they are each on separate lines.
The program will run an algorithm for each line with the search type given.

This is a sample output of the program if the <search_type> is "astar1" and the <input_file> contains one line reading "* 2 3 1 7 5 4 6 8".
Input state:
* 2 3
1 7 5
4 6 8

1 2 3
* 7 5
4 6 8

1 2 3
4 7 5
* 6 8

1 2 3
4 7 5
6 * 8

1 2 3
4 * 5
6 7 8

Goal Reached
Number of moves = 4
Number of states enqueued = 4

Heuristic Analysis:
Heuristic 1 (h1) will always have a lower value than heuristic 2 (h2). H2 is the total distance that all tiles would have to move to get to the goal state. H1 is simply how many tiles are out of place. For every tile of out place, H1 "gains" a value of 1. But, h2 "gains" at least 1 since a tile cannot be less than one space away and still be out of place. H2 will most likely "gain" even more from each out of place tile since there is a decent chance that an out of place tile is not sitting right next to its goal position.
Therefore, h2(n) >= h1(n) for all n. Because of this, we can say that h2 dominates h1 and that h2 is always the better heuristic to choose when running the A* algorithm, especially when the solutions get more and more complicated.