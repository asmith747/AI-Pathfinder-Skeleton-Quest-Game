// Enemy class
// Creates Enemy bot map, enemy inital location, enemy final location, and 
// updates final node when random timer is met

//imports
import java.util.ArrayList;
import java.util.Random;

public class Enemy {

	// New Pathfinder for enemy
	Pathfinder pathInfoEnemy = new Pathfinder();
	// Path Node where enemy is
	PathNode enemy = new PathNode();

	boolean alive = true; // Tells whether enemy is alive or dead
	int finalNode = 0;
	int mouseClickX; // Final node update x
	int mouseClickY; // Final node update y
	// 40x40 grid
	int w = 40;
	int h = 40;
	int distBetween;

	// Count to determine when random location spawns
	int count;

	// Random variables for random initial location, initial and updated final node,
	// and timer for when the enemy moves
	Random random = new Random();
	Random randomXY = new Random();
	Random start = new Random();
	int rand;
	int randXY;
	int randStart;

	// End node for path
	PathNode enemyFinalNode;

	public Enemy() {
		// Get random initial Node
		mouseClickX = randomXY.nextInt(1000);
		mouseClickY = randomXY.nextInt(1000);
		// Timer for random location spawn
		rand = random.nextInt(80) + 20;
		randStart = start.nextInt(1599) + 1;
		distBetween = 1000 / w;
	}

	// Display Enemy
	void display() {
		count++;

		// Once the count reaches the random number, move the final node
		if (alive) {
			if (count == rand) {
				updateFinal();
				rand = random.nextInt(80) + 20;
				count = 0;
			}
		}

		// Set final node
		finalNode = enemyFinalNode();
		
		// Keep getting new final node until final node is on movable location and until
		// final node is not below the change map marker for the main character
		while (pathInfoEnemy.nodes.get(finalNode).move == false || finalNode > 1440) {
			updateFinal();
			finalNode = enemyFinalNode();
		}

		enemyFinalNode = pathInfoEnemy.nodes.get(finalNode);

		ArrayList<PathNode> path = new ArrayList<PathNode>();

		// if the bot isnt at the final node and is alive, search for next path
		if (alive) {
			if (enemy != enemyFinalNode) {
				// get path
				path = pathInfoEnemy.search(enemy, enemyFinalNode);

				// moves backwards through path arrayList as the path starts from end node
				if (path.size() > 1)
					enemy = path.get(path.size() - 2);
			}
		}
	}

	// create node map for enemy
	void createMap() {
		pathInfoEnemy = new Pathfinder();
		pathInfoEnemy.createNodes(40, 40, distBetween);

		// randStart puts enemy at random starting location
		enemy = pathInfoEnemy.nodes.get(randStart);
	}

	// Return int to search in nodes array to get location on map of final
	int enemyFinalNode() {

		int x = mouseClickX / distBetween;
		int y = mouseClickY / distBetween;

		return x + y * w;
	}

	// Update final node once count is reached
	public void updateFinal() {
		randXY = randomXY.nextInt(1000);
		mouseClickX = randXY;
		randXY = randomXY.nextInt(1000);
		mouseClickY = randXY;

	}
}
