import java.util.Random;

public class Confetti {

	Random random = new Random(); // Used for speed, y position, and initial x
	float ySpeed; // Speed confetti falls
	int x; // X position
	float y; // Y position

	// Colors 
	int r = 0;
	int g = 0;
	int b = 0;

	// Default class
	Confetti() {
		y = random.nextInt(1000); // Random initial x and y
		x = random.nextInt(1000);
		ySpeed = (random.nextFloat() * 20) + 5; //Random speed

		// Random color 
		r = random.nextInt(255);
		g = random.nextInt(255);
		b = random.nextInt(255);

	}

	// Update speed and check for past bottom of screen
	void updateConfetti() {
		y += ySpeed;
		if (y > 1000) {
			y = 0;
			ySpeed = (random.nextFloat() * 20) + 1;
			x = random.nextInt(1000);
		}
	}

	// Confetti shaped are built in main class, this updates position 
	void display() {
		updateConfetti();
	}

}