package racingGame;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Ellipse2D;

@SuppressWarnings("serial")
public class Circle extends Ellipse2D.Double{
	
	private static final int RADIUS = 15;
	private Color colour;
	
	public Circle(Point centre){
		super(centre.x-RADIUS, centre.y-RADIUS, 2*RADIUS, 2*RADIUS);
	}

	public Color getColour(){
		return colour;
	}
}