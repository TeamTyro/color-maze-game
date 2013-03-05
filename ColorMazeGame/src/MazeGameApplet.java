import java.applet.Applet;
import java.awt.BorderLayout;


public class MazeGameApplet extends Applet {
	private static final long serialVersionUID = 1L;
	private MazeGame game;
	
	public MazeGameApplet() {
		
	}
	
	public void init() {
		game = new MazeGame();
		setLayout(new BorderLayout());
		add(game, BorderLayout.CENTER);
	}
	
	public void start() {
		game.start();
	}
	
	public void stop() {
		
	}
}