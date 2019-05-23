# AI-Pathfinder-Skeleton-Quest-Game

![Skeleton Quest](https://user-images.githubusercontent.com/46660535/58275415-93b2ad80-7d5a-11e9-955b-bec7022c0eec.JPG)


All code is produced solely by Austin smith. 

Written in eclipse and for the visual aspect uses processing from processing.org

A 2D games that utilizes a custom AI Pathfinder and Markov generator to generate maps. Includes a single quest where the user must eliminate skeletons.

The map is generated using a 20x20 grid of different sprites. They are inserted and once the appropriate formation occurs the program takes a picture of it and then uses it as a background.

 Final Project - The Silent Forest: An AI Pathfinder Application
 
 NOTES:
  - This project showcases the use and necessity of AI Pathfinder in games. 
  	The pathfinder uses best first search in which the path is generated 
  	by traversing from the beginning node to the final node using informed data (distance)
  - It is a simple quest in which the user must eliminate skeletons to win the game.
  - The forest in map two is randomly generated via a markov process in which the
  	initial state is the bottom forest in map one (foliage under bottom fence).
  - Although the algorithm is an original creation, I got all the knowledge necessary to build 
  	this AI from the links below.
  
  
  Instructions:
  - To move around simply click a location in which the character can actually move to 
  - To begin quest, navigate character to NPC in map one
  - To eliminate an enemy, press SPACEBAR while nearby the enemy
  - Proceed to map one after completing quest to win game
  
  
  Links and Citations:
  Best First Search pseudocode
  - https://courses.cs.washington.edu/courses/cse326/03su/homework/hw3/bestfirstsearch.html
  - https://medium.com/omarelgabrys-blog/path-finding-algorithms-f65a8902eb40
  - https://www.researchgate.net/figure/Pseudocode-for-Best-First-Search-algorithm_fig1_315347498  
  
  Grid Based graph pathfinding (assisted with pathNode variables)
  - http://airccse.org/journal/acij/papers/4213acij05.pdf
  
  Euclidean distance calculation
  - http://theory.stanford.edu/~amitp/GameProgramming/Heuristics.html
  
  AI Programming Wisdom Textbook (Basic knowledge of AI Pathfinder)
  - http://planiart.usherbrooke.ca/files/Rabin%202002%20-%20AI%20Game%20Programming%20Wisdom.pdf
  
  Sprites
  - https://opengameart.org/content/basic-map-32x32-by-silver-iv
  - https://itch.io/t/12490/here-are-some-free-pixel-trees
  - https://osbot.org/forum/topic/98964-req-2d-character-sprite-sheet/
