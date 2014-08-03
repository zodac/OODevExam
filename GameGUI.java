package racingGame;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.UIManager;

@SuppressWarnings("serial")
public class GameGUI extends JFrame{
	
	private GameScreen game = new GameScreen();
	
	public GameGUI(){
		setStyleToWindows();
        setTitle("OODev Exam - Racing Game");
        setLocation(450, 200);
        setSize(400, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        add(game);
        addKeyListener(new Steering());
        setResizable(false);
        setVisible(true);
	}
	
	private void setStyleToWindows() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	private class Steering extends KeyAdapter {
		public void keyPressed(KeyEvent event){
			int keyCode = event.getKeyCode();
			
			if(keyCode == KeyEvent.VK_UP)
				game.speedUp();
			else if(keyCode == KeyEvent.VK_DOWN)
				game.slowDown();
			
			if(keyCode == KeyEvent.VK_LEFT)
				game.playerCar.turnLeft();
			if(keyCode == KeyEvent.VK_RIGHT)
				game.playerCar.turnRight();
		}
	}

}
