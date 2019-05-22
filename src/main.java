/*
 * Austin Smith 
 * Final Project - The Silent Forest: An AI Pathfinder Application
 * 
 * NOTES:
 *  - This project showcases the use and necessity of AI Pathfinder in games. 
 *  	The pathfinder uses best first search in which the path is generated 
 *  	by traversing from the beginning node to the final node using informed data (distance)
 *  - It is a simple quest in which the user must eliminate skeletons to win the game.
 *  - The forest in map two is randomly generated via a markov process in which the
 *  	initial state is the bottom forest in map one (foliage under bottom fence).
 *  - Although the algorithm is an original creation, I got all the knowledge necessary to build 
 *  	this AI from the links below.
 *  
 *  
 *  Instructions:
 *  - To move around simply click a location in which the character can actually move to 
 *  - To begin quest, navigate character to NPC in map one
 *  - To eliminate an enemy, press SPACEBAR while nearby the enemy
 *  - Proceed to map one after completing quest to win game
 *  
 *  
 *  Links:
 *  Best First Search pseudocode
 *  - https://courses.cs.washington.edu/courses/cse326/03su/homework/hw3/bestfirstsearch.html
 *  - https://medium.com/omarelgabrys-blog/path-finding-algorithms-f65a8902eb40
 *  - https://www.researchgate.net/figure/Pseudocode-for-Best-First-Search-algorithm_fig1_315347498
 *  
 *  Grid Based graph pathfinding (assisted with pathNode variables)
 *  - http://airccse.org/journal/acij/papers/4213acij05.pdf
 *  
 *  Euclidean distance calculation
 *  - http://theory.stanford.edu/~amitp/GameProgramming/Heuristics.html
 *  
 *  AI Programming Wisdom Textbook (Basic knowledge of AI Pathfinder)
 *  - http://planiart.usherbrooke.ca/files/Rabin%202002%20-%20AI%20Game%20Programming%20Wisdom.pdf
 *  
 *  Sprites
 *  - https://opengameart.org/content/basic-map-32x32-by-silver-iv
 *  - https://itch.io/t/12490/here-are-some-free-pixel-trees
 *  - https://osbot.org/forum/topic/98964-req-2d-character-sprite-sheet/
 */

// imports
import processing.core.*;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import processing.core.PImage;

public class main extends PApplet {

	int markovOrder = 3;
	MarkovGen<Integer> forestGenerator = new MarkovGen<Integer>();

	// forestArray is used for initial forest state for markov generation (forest is at bottom of map one)
	// 0 = nothing, 1 = tree, 2 = rock, 3 = red flower, 4 = white flower
	static Integer[] forestArray = { 0, 0, 0, 0, 0, 1, 3, 0, 0, 0, 1, 0, 0, 1, 2, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 4, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 2,
			0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 4, 0, 0, 0, 0, 0, 0,
			0 };
	static ArrayList<Integer> forest = new ArrayList<Integer>(Arrays.asList(forestArray));
	ArrayList<Integer> generatedForest = new ArrayList<Integer>();

	//Hardcoded values for map one non movable nodes
	Integer[] moveAble = { 17, 22, 57, 62, 97, 102, 137, 142, 177, 182, 217, 222, 257, 262, 297, 302, 337, 342, 377,
			382, 417, 422, 457, 462, 497, 502, 537, 542, 577, 582, 615, 616, 617, 622, 623, 624, 627, 628, 629, 630,
			631, 632, 633, 640, 641, 642, 643, 644, 645, 646, 647, 648, 649, 650, 651, 652, 653, 654, 655, 664, 667,
			672, 680, 681, 682, 683, 684, 685, 686, 687, 688, 689, 690, 691, 692, 693, 694, 695, 704, 705, 706, 707,
			712, 713, 714, 715, 716, 717, 718, 719, 720, 1002, 1003, 1032, 1033, 1037, 1036, 1040, 1041, 1042, 1043,
			1044, 1045, 1046, 1047, 1048, 1049, 1050, 1051, 1052, 1053, 1054, 1055, 1056, 1057, 1058, 1059, 1060, 1061,
			1062, 1063, 1064, 1065, 1066, 1067, 1068, 1069, 1070, 1071, 1072, 1073, 1074, 1075, 1076, 1077, 1078, 1079,
			1080, 666, 626, 586, 546, 506, 466, 426, 386, 346, 306, 266, 226, 227, 187, 188, 148, 149, 150, 151, 191,
			192, 232, 233, 273, 274, 275, 276, 277, 317, 357, 397, 437, 477, 517, 557, 556, 555, 554, 553, 593, 633,
			673, 679, 678, 677, 676, 675, 674, 673, 672 };

	boolean detail = true; // show rocks, flowers, tree stumps
	boolean enemySpawn = true; // Toggle enemies

	Pathfinder pathInfo = new Pathfinder();; // Pathfinder for mainCharacter Node
	PathNode mainCharacter = new PathNode(); // Main Character

	int mouseClick = 0; // Final Node (Updated with mousePressed())
	int wid = 40; // 40x40 grid of Nodes
	int hght = 40;

	int mouseClickX = 30; // Initial Final Node x and y
	int mouseClickY = 490;
	int distBetween; // distBetween is the distance between each node, used for positioning of nodes
						// and conversion of x and y position for character sprites. Also used for
						// scaling the nodes when the map is created

	int map = 1; // Determines which map is being drawn (1 or 2)

	Enemy[] enemies; // Holder of enemy objects
	int numEnemy = 6; // number of enemies

	Confetti[] confetti;
	int numConfetti = 500;

	boolean speak = true; // For NPC chat
	boolean spoke = false; // If you have spoke to NPC
	boolean favor = false; // If favor has been accepted
	boolean finalSpeak = false; // Last chat
	boolean win = false; // If all skeletons are killed
	boolean gameMenu = true; // For main menu screen
	boolean mapSwitch = false; // If character has won in the map 2 without going to map 1, confetti wont show.
								// Once map has been switched, confetti will now be displayed in all maps

	// PImage objects for character sprites
	PImage[] characterDown = new PImage[3];
	PImage[] characterUp = new PImage[4];
	PImage[] characterRight = new PImage[3];
	PImage[] characterLeft = new PImage[3];

	PImage[] enemyUp = new PImage[2];
	PImage[] enemyDown = new PImage[2];
	PImage[] enemyRight = new PImage[2];
	PImage[] enemyLeft = new PImage[2];
	PImage bones = new PImage();

	PImage npc = new PImage();

	// PImage objects for trees and grass
	PImage tree = new PImage();
	PImage grass = new PImage();

	// PImage objects for path tiles
	PImage topL = new PImage();
	PImage topM = new PImage();
	PImage topR = new PImage();
	PImage leftM = new PImage();
	PImage rightM = new PImage();
	PImage bottomL = new PImage();
	PImage bottomM = new PImage();
	PImage bottomR = new PImage();
	PImage middleUpCornerR = new PImage();
	PImage middleUpCornerL = new PImage();
	PImage topCornerLeft = new PImage();
	PImage topCornerRight = new PImage();
	PImage pathB = new PImage();
	PImage pathT = new PImage();

	// PImage objects for fence
	PImage fenceUp = new PImage();
	PImage fenceEndL = new PImage();
	PImage fenceEndR = new PImage();
	PImage fenceUpL = new PImage();
	PImage fenceUpR = new PImage();
	PImage fenceM = new PImage();

	// PImage objects for extra detail
	PImage bushLarge = new PImage();
	PImage flowWhite = new PImage();
	PImage rock = new PImage();
	PImage stumpTree = new PImage();
	PImage background = new PImage();

	// PImage object for house
	PImage house = new PImage();

	PImage chatbox = new PImage();
	PImage logo = new PImage();

	// counts for keeping track of all the main character sprites (movements)
	int upCount, downCount, sideCountR, sideCountL;
	int eUpCount, eDownCount, eSideCountR, eSideCountL;

	// Values to locate where in the map each PImage should be placed. (numbers
	// represent nodes as if they were 20x20, instead of 40x40)
	// Trees
	int[] initialTrees = new int[] { 6, 12, 15, 18, 21, 28, 35, 43, 53, 59, 64, 86, 103, 116, 120, 135, 147, 159, 161,
			175, 180, 216, 240, 276, 298, 319, 321, 353, 378, 381, 393, 395 };

	// Paths (Abbreviated names, Example: TL = top left or BL = bottom left)
	int[] pathTop = new int[] { 10, 30, 50, 70, 90, 110, 130, 150, 250, 270, 330, 350, 370, 390 };
	int[] pathBottom = new int[] { 11, 31, 51, 71, 91, 111, 131, 151, 251, 271, 291, 311, 331, 351, 371, 391 };
	int TL = 169;
	int[] TM = new int[] { 189, 209 };
	int TR = 229;
	int[] LM = new int[] { 170, 171, 201, 202, 203, 204, 205, 206, 207, 208, 308, 309 };
	int[] RM = new int[] { 181, 182, 183, 184, 185, 186, 187, 188, 230, 231, 288, 289 };
	int BL = 172;
	int[] BM = new int[] { 192, 212 };
	int BR = 232;
	int MUCL = 290;
	int MUCR = 310;

	int NPC = 289;

	// Fences (Abbreviated names, Example: FM = fence middle or FEL = fence end
	// facing left)
	int[] FM = new int[] { 9, 14, 29, 34, 49, 54, 69, 74, 89, 94, 109, 114, 129, 134, 154, 174, 194, 214, 234, 254, 274,
			294, 314, 334, 349, 354, 369, 374, 389, 394 };
	int[] FULS = new int[] { 161, 162, 163, 164, 165, 166, 167 };
	int[] FURS = new int[] { 221, 222, 223, 224, 225, 226, 227 };
	int[] FEL = new int[] { 149, 168, 269 };
	int[] FER = new int[] { 228, 249, 329 };
	int FUL = 248;
	int FUR = 148;

	// Details
	int[] bushL = new int[] { 190, 191, 210, 211 };
	int[] rocks = new int[] { 55, 79, 199, 296, 300, 357, 399 };
	int[] treeStump = new int[] { 17, 136, 238, 315 };
	int[] whiteFlow = new int[] { 19, 58, 158, 260, 376, 399 };

	// Values for Map 2 images
	// Paths
	int[] LM2 = new int[] { 370, 390 };
	int[] RM2 = new int[] { 371, 391 };
	int TCR = 351;
	int TCL = 350;

	int picCount = 0;
	int confettiCount = 0;

	public void settings() {
		loadImages(); // loads all images into their PImage objects
		resizeImages(); // Resizes all the images to scale
		size(1000, 1000);
		distBetween = width / wid; // distance between nodes, used for getting final node, outputting x and y
									// values for sprites, etc.
	}

	public void setup() {
		// Generate new forest
		forestGenerator.train(forest);
		forestGenerator.calculateProb(markovOrder, false);
		forestGenerator.setData("Words", false);
		// 400 since initial state is 100 so must generate length of 400 for 20x20 grid
		generatedForest = forestGenerator.generate(400);

		imageMode(CENTER);
		rectMode(CENTER);
		frameRate(6);
		if (enemySpawn)
			createEnemyMap(); // Creates map for enemies and initializes their initial position
		createMap(); // Creates map for main character and initializes position
	}

	public void draw() {
		mouseClick = mouseFinalNode(); // Final node is updated with mousePressed

		// Map 1 = initial map, draws map, main character, and trees

		if (picCount == 0) {
			drawMap1();
			drawTree1();
			save("src/mid/background.png");
			background = loadImage(getPath("mid/background.png"));
			picCount = 1;
		}

		if (map == 1) {
			image(background, width / 2, height / 2, width, height);
			drawCharacter();
			drawChat();
		}

		// Map 2 = next map (skeleton forest), draws map, main character, enemies and
		// trees
		if (map == 2) {
			drawMap2();
			if (enemySpawn)
				displayEnemy();
			drawCharacter();

			drawTree2();
		}

		// Draw Confetti
		if (win && mapSwitch) {
			for (int i = 0; i < confetti.length; i++) {
				noStroke();
				fill(confetti[i].r, confetti[i].g, confetti[i].b);

				if (confettiCount == 0)
					rect(confetti[i].x, confetti[i].y, 6, 9);

				if (confettiCount == 1) {
					rect(confetti[i].x, confetti[i].y, 9, 6);
					confettiCount = -1;
				}
				confettiCount++;
				confetti[i].display();
			}
		}
	}

	// If character is 1 node or closer from any skeleton and they hit spacebar, the
	// skeleton will die
	void attack() {
		for (int h = 0; h < enemies.length; h++) {
			if ((mainCharacter.x - enemies[h].enemy.x) <= 25 && (mainCharacter.x - enemies[h].enemy.x) >= -25
					&& (mainCharacter.y - enemies[h].enemy.y) <= 25 && (mainCharacter.y - enemies[h].enemy.y) >= -25) {
//				if (enemies[h].enemy == enemies[h].enemyFinalNode) {
				enemies[h].alive = false;
				break;
//				}
			}
		}
	}

	// creates the nodes and the
	void createMap() {
		// set
		pathInfo.createNodes(40, 40, distBetween);

		// puts bot's starting point at the corner of the screen as the bsf(pathfinder
		// nodes (0) is the first square on the map
		mainCharacter = pathInfo.nodes.get(802);

		// set nodes to non movable
		if (map == 1) {
			for (int q = 0; q < moveAble.length; q++) {
				pathInfo.nodes.get(moveAble[q]).move = false;
			}
		}
	}

	// gets final node (updated with mousePressed() function)
	int mouseFinalNode() {
		int x = mouseClickX / distBetween; // gets the x location of node in terms of 40x40 grid
		int y = mouseClickY / distBetween; // gets the y location of node in terms of 40x40 grid

		return x + y * wid; // outputs value (value is the location in the arrayList nodes in which the
							// final node is now located)
	}

	// mousePressed updates the x and y of the final node and will set the new final
	// noode (new path
	public void mousePressed() {
		gameMenu = false;
		mouseClickX = mouseX;
		mouseClickY = mouseY;
	}

	// draw map 1 (if( map ==1))
	void drawMap1() {

		int nodeCount = 0; // keeps track of 20x20 node position

		// count used for all the arrays pertaining to PImages so the next value can be
		// considered
		int pathCountT = 0;
		int pathCountB = 0;
		int countTM = 0;
		int countLM = 0;
		int countRM = 0;
		int countBM = 0;
		int countFM = 0;
		int countFUL = 0;
		int countFUR = 0;
		int countFER = 0;
		int countBushL = 0;
		int countWF = 0;
		int countR = 0;
		int countTS = 0;

		pushMatrix();
		translate(-25, -25);
		// map 20x20 grid
		for (int i = 1; i <= 20; i++) {
			for (int y = 1; y <= 20; y++) {

				nodeCount++;

				// If nodeCount is equal to any of the integers above lines 99-138 (for PImage
				// display) then display the specific image and add a count to the arrayCount so
				// compiler knows the next location to check for specific Image
				if (nodeCount == TL)
					image(topL, 50 * i, 50 * y);
				else if (nodeCount == TR)
					image(topR, 50 * i, 50 * y);
				else if (nodeCount == BL)
					image(bottomL, 50 * i, 50 * y);
				else if (nodeCount == BR)
					image(bottomR, 50 * i, 50 * y);
				else if (nodeCount == MUCR)
					image(middleUpCornerR, 50 * i, 50 * y);
				else if (nodeCount == MUCL)
					image(middleUpCornerL, 50 * i, 50 * y);
				else if (nodeCount == NPC) {
					image(rightM, 50 * i, 50 * y);
					image(npc, 50 * i, 50 * y);
				} else if (nodeCount == pathTop[pathCountT]) {
					image(pathT, 50 * i, 50 * y);
					pathCountT++;
					if (pathCountT == pathTop.length)
						pathCountT = 0;
				} else if (nodeCount == pathBottom[pathCountB]) {
					image(pathB, 50 * i, 50 * y);
					pathCountB++;
					if (pathCountB == pathBottom.length)
						pathCountB = 0;
				} else if (nodeCount == TM[countTM]) {
					image(topM, 50 * i, 50 * y);
					countTM++;
					if (countTM == TM.length)
						countTM = 0;
				} else if (nodeCount == LM[countLM]) {
					image(leftM, 50 * i, 50 * y);
					countLM++;
					if (countLM == LM.length)
						countLM = 0;
				} else if (nodeCount == RM[countRM]) {
					image(rightM, (50 * i), (50 * y));
					countRM++;
					if (countRM == RM.length)
						countRM = 0;
				} else if (nodeCount == BM[countBM]) {
					image(bottomM, 50 * i, 50 * y);
					countBM++;
					if (countBM == BM.length)
						countBM = 0;
				} else if (nodeCount == FM[countFM]) {
					image(grass, 50 * i, 50 * y);
					pushMatrix();
					translate(0, -8);
					image(fenceM, 50 * i, 50 * y);
					popMatrix();
					countFM++;
					if (countFM == FM.length)
						countFM = 0;
				} else if (nodeCount == FULS[countFUL]) {
					image(grass, 50 * i, 50 * y);
					pushMatrix();
					translate(5, 0);
					image(fenceUp, 50 * i, 50 * y);
					popMatrix();
					countFUL++;
					if (countFUL == FULS.length)
						countFUL = 0;
				} else if (nodeCount == FURS[countFUR]) {
					image(grass, 50 * i, 50 * y);
					pushMatrix();
					translate(-7, 0);
					image(fenceUp, 50 * i, 50 * y);
					popMatrix();
					countFUR++;
					if (countFUR == FURS.length)
						countFUR = 0;
				} else if (nodeCount == 149) {
					image(grass, 50 * i, 50 * y);
					pushMatrix();
					translate(-8, -9);
					image(fenceEndL, 50 * i, 50 * y);
					popMatrix();
				} else if (nodeCount == 168 || nodeCount == 269) {
					image(grass, 50 * i, 50 * y);
					pushMatrix();
					translate(-8, -9);
					image(fenceEndL, 50 * i, 50 * y);
					popMatrix();
				} else if (nodeCount == 228) {
					image(grass, 50 * i, 50 * y);
					pushMatrix();
					translate(8, -9);
					image(fenceEndR, 50 * i, 50 * y);
					popMatrix();
					countFER++;
					if (countFER == FER.length)
						countFER = 0;
				} else if (nodeCount == 249 || nodeCount == 329) {
					image(grass, 50 * i, 50 * y);
					pushMatrix();
					translate(10, -9);
					image(fenceEndR, 50 * i, 50 * y);
					popMatrix();
					countFER++;
					if (countFER == FER.length)
						countFER = 0;
				} else if (nodeCount == FUL) {
					image(grass, 50 * i, 50 * y);
					pushMatrix();
					translate(-19, 9);
					image(fenceUpL, 50 * i, 50 * y);
					popMatrix();
				} else if (nodeCount == FUR) {
					image(grass, 50 * i, 50 * y);
					pushMatrix();
					translate(19, 9);
					image(fenceUpR, 50 * i, 50 * y);
					popMatrix();
				} else if (nodeCount == FUR) {
					image(grass, 50 * i, 50 * y);
					pushMatrix();
					translate(19, 9);
					image(fenceUpR, 50 * i, 50 * y);
					popMatrix();
				} else if (nodeCount == bushL[countBushL] && detail) {
					image(grass, 50 * i, 50 * y);
					image(bushLarge, 50 * i, 50 * y);
					countBushL++;
					if (countBushL == bushL.length)
						countBushL = 0;
				} else if (nodeCount == rocks[countR] && detail) {
					image(grass, 50 * i, 50 * y);
					image(rock, 50 * i, 50 * y);
					countR++;
					if (countR == rocks.length)
						countR = 0;
				} else if (nodeCount == treeStump[countTS] && detail) {
					image(grass, 50 * i, 50 * y);
					image(stumpTree, 50 * i, 50 * y);
					countTS++;
					if (countTS == treeStump.length)
						countTS = 0;
				} else if (nodeCount == whiteFlow[countWF] && detail) {
					image(grass, 50 * i, 50 * y);
					image(flowWhite, 50 * i, 50 * y);
					countWF++;
					if (countWF == whiteFlow.length)
						countWF = 0;
				} else
					image(grass, 50 * i, 50 * y);

				// house image
				image(house, 815, 260);

				// Displays count on each 20x20 square

//				textSize(20);
//				text(nodeCount, (40000 / 800 * i) - 10, 40000 / 800 * y);

			}
		}

		popMatrix();

		// Visualize all the actual path nodes 40x40
		nodeCount = 0;
//		for (int i = 1; i <= wid; i++) {
//			for (int y = 1; y <= hght; y++) {
//				pushMatrix();
//				translate(-12, -12);
//				noFill();
//				stroke(0);
//				strokeWeight(1);
//				rect(25 * i, 25 * y, 25, 25);
//
//				fill(255);
//				textSize(10);
//				text(nodeCount, (25 * y) - 5, 25 * i);
//				popMatrix();
//				nodeCount++;
//			}
//		}
	}

	// Draw trees (in separate function so they can be displayed in front of
	// character)
	void drawTree1() {
		int treeCount = 0;
		int nodeCount = 0;

		pushMatrix();
		translate(-25, -25);
		for (int i = 1; i <= 20; i++) {
			for (int y = 1; y <= 20; y++) {
				nodeCount++;
				// trees
				if (nodeCount == initialTrees[treeCount] && treeCount < initialTrees.length - 1) {

					image(tree, (50 * i) - 50, (50 * y) - 25);
					treeCount++;
				}

//				textSize(20);
//				text(nodeCount, (50 * i) - 10, 50 * y);
			}
		}
		popMatrix();
	}

	void drawChat() {

		if (gameMenu) {
			stroke(0);
			strokeWeight(4);
			fill(255);
			rect(width / 2, 285, 650, 440);
			rect(width / 2, 317, 650, 1);
			image(logo, width / 2 + 65, 200);
			fill(0);
			textSize(22);
			text("Created By: Austin Smith", width / 2 - 140, 305);
			text("INSTRUCTIONS", width / 2 - 70, 350);
			text("Press SPACEBAR to attack when near an enemy", width / 2 - 250, 385);
			text("Click in movable region to move character around", width / 2 - 270, 415);
			text("To begin quest, talk to NPC", width / 2 - 145, 445);
			text("Click anywhere to begin", width / 2 - 127, 495);
		}

		if (speak && !win) {
			if (mainCharacter.x >= 200) {
				textSize(22);
				fill(0);
				image(chatbox, 795, 390);
				text("Hey, come here!", 692, 385);
				if (mainCharacter.x >= 675 && mainCharacter.x <= 765 && mainCharacter.y >= 390
						&& mainCharacter.y <= 490) {
					spoke = true;
					image(chatbox, 795, 390);
					text("Will you do", 717, 372);
					text("me a favor?", 717, 395);
					stroke(0);
					strokeWeight(3);
					fill(255);
					rect(width / 2 + 25, 575, 400, 120);
					fill(0);
					textSize(25);
					text("Do favor?", width / 2 - 30, 550);
					text("Press  Y for yes", width / 2 - 60, 590);
				}
			}
		}
		if (favor && !finalSpeak && !win) {
			stroke(0);
			strokeWeight(3);
			fill(255);
			rect(width / 2 + 25, 575, 440, 125);
			fill(0);
			textSize(25);
			text("Thank you so much!", width / 2 - 92, 540);
			text("We have been having a problem", width / 2 - 165, 569);
			text("with skeletons in the forest above.", width / 2 - 178, 599);
			text("Please try and get rid of them!", width / 2 - 157, 629);
		}
		if (win) {
			stroke(0);
			strokeWeight(3);
			fill(255);
			rect(width / 2, 575, 380, 130);
			fill(0);
			image(chatbox, 795, 390);
			textSize(22);
			text("Thank you so", 714, 372);
			text("much!", 750, 395);
			textSize(72);
			text("YOU WIN!", width / 2 - 163, 600);
		}
	}

// Map 2 (if (map ==2))

	void drawMap2() {
		int nodeCount = 0;
		int countRM = 0;
		int countLM = 0;

		// draw grass
		pushMatrix();
		translate(-25, -25);

		for (int i = 1; i <= 20; i++) {
			for (int y = 1; y <= 20; y++) {
				nodeCount++;
				image(grass, 50 * y, 50 * i);

				if (nodeCount == TCL)
					image(topCornerLeft, 50 * y, 50 * i);
				else if (nodeCount == TCR)
					image(topCornerRight, 50 * y, 50 * i);
				else if (nodeCount == RM2[countRM]) {
					image(leftM, (50 * y), (50 * i));
					countRM++;
					if (countRM == RM2.length)
						countRM = 0;
				} else if (nodeCount == LM2[countLM]) {
					image(rightM, 50 * y, 50 * i);
					countLM++;
					if (countLM == LM2.length)
						countLM = 0;
				}

				// visualize 20x20 grid
//				noFill();
//				stroke(0);
//				strokeWeight(3);
//				rect(50 * y, 50 * i, 50, 50);

//				textSize(20);
//				text(nodeCount - 1, (50 * y) - 10, 50 * i);
			}
		}
		fill(255);
		textSize(22);
		text("When nearby an enemy", 620, 970);
		text("press SPACEBAR to attack", 620, 995);
		popMatrix();
		nodeCount = 0;

		// Rocks, flowers, white flowers
		for (int y = 1; y <= 20; y++) {
			for (int i = 1; i <= 20; i++) {
				if (nodeCount != 350 && nodeCount != 370 && nodeCount != 390 && nodeCount != 351 && nodeCount != 370
						&& nodeCount != 391) {
					int getDetail = generatedForest.get(nodeCount);
					nodeCount++;

					pushMatrix();
					translate(25, 0);
					if (getDetail == 2) {
						image(rock, (50 * i) - 50, (50 * y) - 25);
					}
					if (getDetail == 3) {
						image(stumpTree, (50 * i) - 50, (50 * y) - 25);
					}
					if (getDetail == 4) {
						image(flowWhite, (50 * i) - 50, (50 * y) - 25);
					}
					popMatrix();
				}
			}
		}

		// Visualize 40x40 path nodes and show count on each node
//		for (int i = 1; i <= wid; i++) {
//			for (int y = 1; y <= hght; y++) {
//				pushMatrix();
//				translate(-12, -12);
//				noFill();
//				stroke(0);
//				strokeWeight(1);
//				rect(25 * i, 25 * y, 25, 25);
//
//				fill(255);
//				textSize(10);
//				text(nodeCount, (25 * y) - 5, 25 * i);
//				popMatrix();
//				nodeCount++;
//			}
//		}
	}

	void drawTree2() {

		int nodeCount = 0;

		for (int y = 1; y <= 20; y++) {
			for (int i = 1; i <= 20; i++) {
				if (nodeCount != 350 && nodeCount != 370 && nodeCount != 390 && nodeCount != 351 && nodeCount != 370
						&& nodeCount != 391) {
					int getDetail = generatedForest.get(nodeCount);
					nodeCount++;

					pushMatrix();
					translate(25, -25);
					if (getDetail == 1)
						image(tree, (50 * i) - 50, (50 * y) - 25);

					popMatrix();
				}
			}
		}
	}

	void drawCharacter() {

		PathNode finalNode = pathInfo.nodes.get(mouseClick);
		ArrayList<PathNode> path = new ArrayList<PathNode>();

		// if the bot isnt at the final, get next path from search and move
		// mainCharacter pathNode
		if (mainCharacter != finalNode) {
			path = pathInfo.search(mainCharacter, finalNode);

			// Goes through the path list backwards to move from beginning node as I set the
			// path starting from the end node
			if (path.size() > 1)
				mainCharacter = path.get(path.size() - 2);
		}

		noStroke();
		pushMatrix();
		translate(13, -5);

		// Move Up animation
		if (finalNode.move == false) {
			image(characterDown[0], mainCharacter.x - wid / distBetween, mainCharacter.y - hght / distBetween);
		} else if (finalNode.y < mainCharacter.y && finalNode.move == true) {
			image(characterUp[upCount], mainCharacter.x - wid / distBetween, mainCharacter.y - hght / distBetween);
			upCount++;
			if (upCount == 4)
				upCount = 0;
		}

		// Move Right animation
		else if (finalNode.x < mainCharacter.x && finalNode.move == true) {
			image(characterLeft[sideCountL], mainCharacter.x - wid / distBetween, mainCharacter.y - hght / distBetween);
			sideCountL++;
			if (sideCountL == 3)
				sideCountL = 0;
		}

		// Move Left animation
		else if (finalNode.x > mainCharacter.x && finalNode.move == true) {

			image(characterRight[sideCountR], mainCharacter.x - wid / distBetween,
					mainCharacter.y - hght / distBetween);
			sideCountR++;
			if (sideCountR == 3)
				sideCountR = 0;
		}

		// Standing still/ moving down animation
		else if (mainCharacter.x / distBetween == finalNode.x / distBetween)

			image(characterDown[0], mainCharacter.x - wid / distBetween, mainCharacter.y - hght / distBetween);
		else {
			image(characterDown[downCount], mainCharacter.x - wid / distBetween, mainCharacter.y - hght / distBetween);
			downCount++;
			if (downCount == 3)
				downCount = 0;
		}
		popMatrix();

		// Go to next map (above)
		if (mainCharacter.y <= 25 && map == 1) {
			resetNodes();
			createMoveables2();

			if (favor) {
				finalSpeak = true;
			}
			map++;
			mouseClickY = (int) mainCharacter.y + 900;
			mouseClickX = (int) mainCharacter.x;
			int newBotNode = mouseClickX / 25;
			mainCharacter = pathInfo.nodes.get(1480 + newBotNode);
		}

		// Go to prev map (below)
		if (mainCharacter.y >= 950 && map == 2) {

			resetNodes();
			createMoveables1();
			if (checkDead()) {
				win = true;
				mapSwitch = true;
			}
			map--;
			mouseClickY = (int) mainCharacter.y - 900;
			mouseClickX = (int) mainCharacter.x;
			int newBotNode = mouseClickX / 25;
			mainCharacter = pathInfo.nodes.get(80 + newBotNode);
		}
	}

	// Creates collision on fences, trees, and house
	void createMoveables1() {
		for (int q = 0; q < moveAble.length; q++) {
			pathInfo.nodes.get(moveAble[q]).move = false;
		}
	}

	// Creates collision on trees, rocks, and tree stump in forest
	void createMoveables2() {
		int x = -1;
		int y = 0;
		int yCount = 0;

		for (int q = 0; q < generatedForest.size(); q++) {
			x++;
			if (generatedForest.get(q) != 0 && generatedForest.get(q) != 4 && q < 360) {

				pathInfo.nodes.get((y * 2 * 40) + (x * 2)).move = false;
				pathInfo.nodes.get((y * 2 * 40) + (x * 2) + 1).move = false;
				pathInfo.nodes.get((y * 2 * 40) + (x * 2) + 40).move = false;
				pathInfo.nodes.get((y * 2 * 40) + (x * 2) + 41).move = false;

				for (int e = 0; e < enemies.length; e++) {
					enemies[e].pathInfoEnemy.nodes.get((y * 2 * 40) + (x * 2)).move = false;
					enemies[e].pathInfoEnemy.nodes.get((y * 2 * 40) + (x * 2) + 1).move = false;
					enemies[e].pathInfoEnemy.nodes.get((y * 2 * 40) + (x * 2) + 40).move = false;
					enemies[e].pathInfoEnemy.nodes.get((y * 2 * 40) + (x * 2) + 41).move = false;
				}
			}

			if (x == 20) {
				yCount++;
				x = 0;
				y++;
			}
		}
	}

	void resetNodes() {

		for (int q = 0; q < moveAble.length; q++) {
			pathInfo.nodes.get(moveAble[q]).move = true;
		}
		for (int x = 0; x < enemies.length; x++) {
			for (int d = 0; d < enemies[x].pathInfoEnemy.nodes.size(); d++) {
				enemies[x].pathInfoEnemy.nodes.get(d).move = true;
				pathInfo.nodes.get(d).move = true;
			}
		}

	}

	boolean checkDead() {
		boolean deadAlive = true;
		for (int h = 0; h < enemies.length; h++) {
			if (enemies[h].alive) {
				deadAlive = false;
				break;
			}
		}
		return deadAlive;
	}

// display enemy

	void displayEnemy() {
		noStroke();
		pushMatrix();
		translate(12, 5);

		for (int i = 0; i < enemies.length; i++) {
			enemies[i].display(); // This function basically creates a random final node at random times

			if (!enemies[i].alive) {
				pushMatrix();
				translate(2, 10);
				image(bones, enemies[i].enemy.x - wid / distBetween, enemies[i].enemy.y - hght / distBetween);
				popMatrix();
			} else if (enemies[i].enemyFinalNode.y < enemies[i].enemy.y) {
				image(enemyUp[eUpCount], enemies[i].enemy.x - wid / distBetween,
						enemies[i].enemy.y - hght / distBetween);
				eUpCount++;
				if (eUpCount == 2)
					eUpCount = 0;
			} else if (enemies[i].enemyFinalNode.y > enemies[i].enemy.y) {
				image(enemyDown[eUpCount], enemies[i].enemy.x - wid / distBetween,
						enemies[i].enemy.y - hght / distBetween);
				eDownCount++;
				if (eDownCount == 2)
					eDownCount = 0;
			} else if (enemies[i].enemyFinalNode.x > enemies[i].enemy.x) {
				image(enemyRight[eSideCountR], enemies[i].enemy.x - wid / distBetween,
						enemies[i].enemy.y - hght / distBetween);
				eSideCountR++;
				if (eSideCountR == 2)
					eSideCountR = 0;
			} else if (enemies[i].enemyFinalNode.x < enemies[i].enemy.x) {
				image(enemyLeft[eSideCountL], enemies[i].enemy.x - wid / distBetween,
						enemies[i].enemy.y - hght / distBetween);
				eSideCountL++;
				if (eSideCountL == 2)
					eSideCountL = 0;
			} else {
				image(enemyDown[0], enemies[i].enemy.x - wid / distBetween, enemies[i].enemy.y - hght / distBetween);
			}
		}

		popMatrix();
	}

	public void keyPressed() {
		if (key == ' ') {
			attack();
		}
		if (key == 'Y' || key == 'y') {
			if (spoke) {
				speak = false;
				favor = true;
			}
		}
	}

	// Initialize enemies and create map for all enemies
	void createEnemyMap() {

		enemies = new Enemy[numEnemy];
		confetti = new Confetti[numConfetti];

		for (int i = 0; i < confetti.length; i++) {
			confetti[i] = new Confetti();
		}

		for (int i = 0; i < enemies.length; i++) {
			enemies[i] = new Enemy();
			enemies[i].createMap();
		}
	}

	
	public static void main(String[] args) {
		PApplet.main("main");
	}

	
	String getPath(String filename) {
		String filepath = "";
		try {
			filepath = URLDecoder.decode(getClass().getResource(filename).getPath(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return filepath;
	}

	
	// resize images (moved down here for better organization)

	void resizeImages() {
		grass.resize(0, 51);
		tree.resize(100, 100);
		pathB.resize(0, 51);
		pathT.resize(0, 51);
		topL.resize(0, 51);
		topM.resize(0, 51);
		topR.resize(0, 51);
		leftM.resize(0, 51);
		rightM.resize(0, 52);
		bottomL.resize(0, 51);
		bottomM.resize(0, 51);
		bottomR.resize(0, 51);
		house.resize(500, 500);
		middleUpCornerR.resize(0, 51);
		middleUpCornerL.resize(0, 51);
		topCornerLeft.resize(0, 51);
		topCornerRight.resize(0, 51);
		fenceM.resize(52, 41);
		fenceUp.resize(0, 60);
		fenceEndL.resize(0, 37);
		fenceEndR.resize(0, 37);
		fenceUpL.resize(0, 68);
		fenceUpR.resize(0, 69);
		bones.resize(0, 30);
		npc.resize(0, 60);
		chatbox.resize(250, 100);
		logo.resize(0, 650);

		if (detail) {
			flowWhite.resize(0, 58);
			rock.resize(0, 58);
			stumpTree.resize(0, 58);
			bushLarge.resize(0, 65);
		}
	}

// load images (moved down here for better organization)

	void loadImages() {
		characterDown[0] = loadImage(getPath("mid/Character2.png"));
		characterDown[1] = loadImage(getPath("mid/Character (10).png"));
		characterDown[2] = loadImage(getPath("mid/Character (12).png"));

		characterUp[0] = loadImage(getPath("mid/Character (2).png"));
		characterUp[1] = loadImage(getPath("mid/Character (4).png"));
		characterUp[2] = loadImage(getPath("mid/Character (6).png"));
		characterUp[3] = loadImage(getPath("mid/Character (8).png"));

		characterRight[0] = loadImage(getPath("mid/CharacterSideL.png"));
		characterRight[1] = loadImage(getPath("mid/Character (14).png"));
		characterRight[2] = loadImage(getPath("mid/Character (15).png"));

		characterLeft[0] = loadImage(getPath("mid/CharacterSide.png"));
		characterLeft[1] = loadImage(getPath("mid/Character (11).png"));
		characterLeft[2] = loadImage(getPath("mid/Character (16).png"));

		enemyDown[0] = loadImage(getPath("mid/SU1.png"));
		enemyDown[1] = loadImage(getPath("mid/SU2.png"));

		enemyRight[0] = loadImage(getPath("mid/SR1.png"));
		enemyRight[1] = loadImage(getPath("mid/SR2.png"));

		enemyLeft[0] = loadImage(getPath("mid/SL1.png"));
		enemyLeft[1] = loadImage(getPath("mid/SL2.png"));

		enemyUp[0] = loadImage(getPath("mid/SU1.png"));
		enemyUp[1] = loadImage(getPath("mid/SU2.png"));
		bones = loadImage(getPath("mid/bones.png"));

		npc = loadImage(getPath("mid/NPC.png"));

		tree = loadImage(getPath("mid/trees (2).png"));

		pathB = loadImage(getPath("mid/bottom.png"));
		pathT = loadImage(getPath("mid/top1.png"));

		grass = loadImage(getPath("mid/Ground.JPG"));

		topL = loadImage(getPath("mid/TL.png"));
		topM = loadImage(getPath("mid/TM.png"));
		topR = loadImage(getPath("mid/TR.png"));
		leftM = loadImage(getPath("mid/LM.png"));
		rightM = loadImage(getPath("mid/RM.png"));
		bottomL = loadImage(getPath("mid/BL.png"));
		bottomM = loadImage(getPath("mid/BM.png"));
		bottomR = loadImage(getPath("mid/BR.png"));
		middleUpCornerR = loadImage(getPath("mid/MUCR.png"));
		middleUpCornerL = loadImage(getPath("mid/MUCL.png"));
		topCornerRight = loadImage(getPath("mid/TCR.png"));
		topCornerLeft = loadImage(getPath("mid/TCL.png"));

		fenceM = loadImage(getPath("mid/FM.png"));
		fenceUp = loadImage(getPath("mid/FU.png"));
		fenceEndL = loadImage(getPath("mid/FEL.png"));
		fenceEndR = loadImage(getPath("mid/FER.png"));
		fenceUpL = loadImage(getPath("mid/FUL.png"));
		fenceUpR = loadImage(getPath("mid/FUR.png"));

		chatbox = loadImage(getPath("mid/chatbox.png"));
		logo = loadImage(getPath("mid/finalLogo.png"));

		house = loadImage(getPath("mid/house1.png"));

		if (detail) {
			bushLarge = loadImage(getPath("mid/BushLarge.png"));
			flowWhite = loadImage(getPath("mid/FW.png"));
			rock = loadImage(getPath("mid/R.png"));
			stumpTree = loadImage(getPath("mid/TSR.png"));
		}
	}

}