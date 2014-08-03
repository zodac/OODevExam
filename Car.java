package racingGame;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.Random;

@SuppressWarnings("serial")
public class Car extends Rectangle{
	
	private static final Random RAND = new Random();
	
	private Color colour;
	private double speed = 10;
	private CarStatus status = CarStatus.ACTIVE;
	
	private int cooldown = -16;
	private boolean direction = RAND.nextBoolean();
	private CarType type;
	
	public Car(int x, int y, int width, int height, Color colour, CarType type){
		super(x, y, width, height);
		this.colour = colour;
		this.type = type;
	}
	
	public Color getColour(){
		return colour;
	}
	
	public double getSpeed(){
		return speed;
	}
	
	public CarType getType(){
		return type;
	}
	
	public int getCooldown(){
		return cooldown;
	}
	
	public void setCooldown(int cooldown){
		this.cooldown = cooldown;
	}
	
	public boolean getDirection(){
		return direction;
	}
	
	public void setDirection(){
		
		if(x-5 < 100)
			direction = false;
		else if(x+5 > 270)
			direction = true;
		else direction = RAND.nextBoolean();
	}
	
	public void turnLeft(){
		if(x-5 > 100)
			super.setLocation(x-5, y);
	}
	
	public void turnRight(){
		if(x+5 < 270)
			super.setLocation(x+5, y);
	}
	
	public void moveDown(double movement){
		super.setLocation(x, (int) (y+movement));
		if(y > 600)
			status = CarStatus.SLOWER;
	}
	
	public void moveUp(double movement){
		super.setLocation(x, (int) (y-movement));
		if(y < 0)
			status = CarStatus.FASTER;
	}
	
	public CarStatus getStatus(){
		return status;
	}
	
	public void cyanMove(boolean direction){
		if(direction)
			turnLeft();
		else
			turnRight();
		cooldown++;
	}
	
	public void redMove(boolean directionIsLeft){
		cyanMove(directionIsLeft);
	}
}
