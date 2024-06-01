package racingGame;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

@SuppressWarnings("serial")
public class GameScreen extends JPanel {
	
	private static final int UPDATE_TIME_IN_MS = 100;
	private static final Random RAND = new Random();
	BufferedImage curb;
	 
	private List<Car> cars = new ArrayList<>();
	private List<Circle> collisionCircles = new ArrayList<>();
	private int updateCounter = 0;
	private int carsPassed = 0;
	private double currentSpeed = 10;
	private JLabel speedLabel = new JLabel("Speed: " + currentSpeed + ", Passed " + carsPassed + " cars");
	
	public Car playerCar = new Car(185, 500, 30, 50, Color.WHITE, CarType.PLAYER);
	
	public GameScreen(){
		speedLabel.setForeground(Color.WHITE);
		add(speedLabel);
		
		try {
			curb = ImageIO.read(new File("./res/curb.png"));
		} catch (IOException e) {
		}
		
		//Create and start threads (as timers)
		startUpdatingGame();
		checkIfCarShouldBeMade();
		startMovingCars();
		startFrictionSlowDown();
		checkForAICollisions();
		checkForPlayerCollision();
	}
	
	private void startUpdatingGame(){
		Timer gameUpdater = new Timer(UPDATE_TIME_IN_MS, new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				repaint();
				updateCounter++;
			}
		});
		gameUpdater.start();
	}
	
	private void checkIfCarShouldBeMade(){
		Timer carMaker = new Timer(UPDATE_TIME_IN_MS, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//No need for updateCounter to be volatile or methods sync'd
					//since this method is read-only
				if(updateCounter%10 == 0 && RAND.nextBoolean()) {
					chooseCarToBuild();	
				}
			}
		});
		carMaker.start();
	}
	
	private void chooseCarToBuild(){
		int carChoice = RAND.nextInt(3);
		
		if(carChoice == 0){
			cars.add(CarFactory.yellowCar());
		} else if(carChoice == 1){
			cars.add(CarFactory.cyanCar());
		} else{
			cars.add(CarFactory.redCar());
		}
	}
	
	private void startMovingCars(){
		Timer carDriver = new Timer(UPDATE_TIME_IN_MS, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				for(Car car : cars){
					double aiCarSpeed = car.getSpeed();
					
					if(currentSpeed > aiCarSpeed){
						car.moveDown(currentSpeed-aiCarSpeed);
					} else{
						car.moveUp(aiCarSpeed-currentSpeed);
					}
					
					if(car.getType() == CarType.CYAN){
						//Wait for cooldown to finish
						if(car.getCooldown() > 10 && RAND.nextBoolean()){
							car.setCooldown(-16);
							car.setDirection();
						}
						if(car.getCooldown() >= 30){
							car.setCooldown(-16);
							car.setDirection();
						}
						
						//If not on CD
						if(car.getCooldown() < 0){
							car.cyanMove(car.getDirection());
						} else{
							car.setCooldown(car.getCooldown()+1);
						}
					}
					
					//TODO Red cars moving at wrong times...
					else if(car.getType() == CarType.RED){
						double xDistance = carsXDistance(playerCar, car);
						double yDistance = carsYDistance(playerCar, car);
										
						if(xDistance < 30 || yDistance < 10){
							car.redMove((car.x > (playerCar.x+25)));
						}
					}
				}
			}
		});
		carDriver.start();
	}
	
	private void startFrictionSlowDown(){
		Timer frictionSlowDown = new Timer(UPDATE_TIME_IN_MS, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(currentSpeed - 0.5 >= 0){
					currentSpeed -= 0.5;
				}
			}
		});
		frictionSlowDown.start();
	}
	
	private void checkForAICollisions(){
		Timer checkForAICollisions = new Timer(UPDATE_TIME_IN_MS, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int numCars = cars.size();
				
				if(numCars > 1){
					for(int i = 0; i < numCars; i++){
						try{
							Car iCar = cars.get(i);
							
							for(int j = 0; j < numCars; j++){
								if(i != j){
									Car jCar = cars.get(j);
									if(carsOverlap(iCar, jCar)){
										collisionCircles.add(new Circle(new Point((int) iCar.getCenterX(), (int) iCar.getCenterY())));
										collisionCircles.add(new Circle(new Point((int) jCar.getCenterX(), (int) jCar.getCenterY())));
										cars.remove(i);
										cars.remove(j-1);
									}
								}
							}
						} catch (IndexOutOfBoundsException e1){
							
						}
					}
				}
			}
		});
		checkForAICollisions.start();
	}
	
	
	private void checkForPlayerCollision(){
		Timer checkForPlayerCollision = new Timer(UPDATE_TIME_IN_MS, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				for(Car car : cars){
					if(carsOverlap(playerCar, car)){
						resetGame();
					}
				}
			}
		});
		checkForPlayerCollision.start();
	}
	
	private boolean carsOverlap(Car iCar, Car jCar){		
		if(carsXDistance(iCar, jCar) > 30){
			return false;
		}
		
		if(carsYDistance(iCar, jCar) > 50){
			return false;
		}
		return true;
	}
	
	private double carsXDistance(Car iCar, Car jCar){
		return Math.abs(iCar.x-jCar.x);
	}
	
	private double carsYDistance(Car iCar, Car jCar){
		return Math.abs(iCar.y-jCar.y);
	}
	
	//These two could be in the Car class, but I didn't think it made sense to update car
	//and then just call that value back from the object, so moved it here instead.
	
	//Rather than check to see if a key was pressed, I'm going to slow down by 0.5 each update,
	//And just change speed up to 1.5 and slow down to 1.5 to compensate 
	public void speedUp(){
		if(currentSpeed + 1.5 <= 20){
			currentSpeed += 1.5;
		}
	}
	
	public void slowDown(){
		if(currentSpeed - 1.5 >= 0){
			currentSpeed -= 1.5;
		}
	}
	
	private void resetGame(){
		cars = new ArrayList<>();
		updateCounter = 0;
		carsPassed = 0;
		currentSpeed = 10;
		playerCar = new Car(185, 500, 30, 50, Color.WHITE, CarType.PLAYER);
	}
	
	@Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_SPEED);
		
		Rectangle grassLeft = new Rectangle(0, 0, 100, 600);
		Rectangle road = new Rectangle(100, 0, 200, 600);
		Rectangle grassRight = new Rectangle(300, 0, 100, 600);
		
		
		g2.setColor(Color.GREEN);
		g2.fill(grassLeft);
		g2.draw(grassLeft);
		g2.fill(grassRight);
		g2.draw(grassRight);
		
		g2.setColor(Color.GRAY);
		g2.fill(road);
		g2.draw(road);
		
		//TODO Figure out why the curbs aren't showing...
		g2.drawImage(curb, null, 88, 0);
		g2.drawImage(curb, null, 300, 0);
		
		g2.setColor(playerCar.getColour());
		g2.fill(playerCar);
		g2.draw(playerCar);
		
		//Not using an iterator so I can remove cars as I'm checking them from the ArrayList
		for(int i = 0; i < cars.size(); i++){
			Car tempCar = cars.get(i);
			if(tempCar.getStatus() == CarStatus.SLOWER){
				cars.remove(i);
				carsPassed++;
			} else if(tempCar.getStatus() == CarStatus.SLOWER){
				cars.remove(i);
			} else{
				g2.setColor(tempCar.getColour());
				g2.fill(tempCar);
				g2.draw(tempCar);
			}
		}
		
		if(!collisionCircles.isEmpty()){
			for(Circle c : collisionCircles){
				g2.setColor(Color.WHITE);
				g2.fill(c);
				g2.draw(c);
			}
			collisionCircles = new ArrayList<>();
		}
		
		speedLabel.setText("Speed: " + currentSpeed + ", Passed " + carsPassed + " cars");	
		speedLabel.setForeground(Color.WHITE);
	}
}