package racingGame;

import java.awt.Color;
import java.util.Random;

public class CarFactory {
	
	private static final int CAR_WIDTH = 30;
	private static final int CAR_HEIGHT = 50;
	private static final Random RAND = new Random();
	
	public static Car yellowCar(){
		return new Car(RAND.nextInt(170)+100, 5, CAR_WIDTH, CAR_HEIGHT, Color.YELLOW, CarType.YELLOW);
	}
	
	public static Car cyanCar(){
		return new Car(RAND.nextInt(170)+100, 5, CAR_WIDTH, CAR_HEIGHT, Color.CYAN, CarType.CYAN);
	}
	
	public static Car redCar(){
		return new Car(RAND.nextInt(170)+100, 5, CAR_WIDTH, CAR_HEIGHT, Color.RED, CarType.RED);
	}
}