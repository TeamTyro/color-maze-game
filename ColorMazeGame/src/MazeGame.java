/* MazeGame Class
 * By Tyler Compton for Team Tyro
 * 
 * This is a very simple and minimal map game. It's official name is
 * "Color Maze Game."
 */
import java.util.*;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import recording.ActionStamp;

public class MazeGame {
	
	// Directional constants
	private static final int DIR_RIGHT = 1;
	private static final int DIR_LEFT = 2;
	private static final int DIR_UP = 3;
	private static final int DIR_DOWN = 4;
	// Map space constants
	private static final int MAP_START = 4;
	private static final int MAP_SPACE = 1;
	private static final int MAP_BLOCK = 2;
	private static final int MAP_WIN = 3;
	// Map property constants
	private static final int MAP_WIDTH = 16;
	private static final int MAP_HEIGHT = 16;

	private static Random generator = new Random();
	private static int[][] map;	// Universal map array
	
	private static ActionStamp [] recActions;
	private static int currentAction;
	private static int replayAction;
	private static boolean replay;
	
	private static boolean [] keyRefresh;
	
	private static int pX, pY;	// Player x and y (within the map array)

	/* Function main(String args[])
	 * Runs maze creation, sets some variables, and starts
	 * the main loop.
	 */
	public static void main(String args[]) {
		System.out.printf("Cheater's map:\n");
		map = makeMaze();
		printMaze(map);
		
		pX = MAP_WIDTH/2;
		pY = 0;
		keyRefresh = new boolean [6];
		
		recActions = new ActionStamp [500];
		replay = false;
		
		currentAction = 0;
		
		begin();
	}
	
	/* Function begin()
	 * Sets up OpenGL and lwjgl and contains the main loop.
	 */
	private static void begin() {
		try {
			Display.setDisplayMode(new DisplayMode(600,600));
			Display.create();
		} catch (LWJGLException e) {
			e.printStackTrace();
			System.exit(0);
		}
		
		// Init OpenGL
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(-300, 300, -300, 300, 1, -1);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		
		// Start main loop
		while(!Display.isCloseRequested()) {
			// Clears screen and depth buffer
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
			
			// Rendering
			render();
			
			if(replay) {
				if(replayAction < currentAction) {
					replayGame(recActions, replayAction);
					replayAction++;
				}
			} else {
				checkKeys();
			}
			
			Display.update();
		}
		
		Display.destroy();
	}
	
	private static void replayGame(ActionStamp [] s_recActions, int currAction) {
		movePlayer(s_recActions[currAction].getAction(), pX, pY, map);
		try {
		    Thread.sleep(100);
		} catch(InterruptedException ex) {
		    Thread.currentThread().interrupt();
		}
	}
	
	/* Function render()
	 * Draws all visible objects.
	 */
	private static void render() {
		int x, y;	// Bottom left corner coordinates (for readability)
		
		// Left box
		x = -300;
		y = -100;
		setColor(pX-1, pY, map);
		GL11.glBegin(GL11.GL_QUADS);
			GL11.glVertex2f(x    ,y    );
			GL11.glVertex2f(x+200,y  +0);
			GL11.glVertex2f(x+200,y+200);
			GL11.glVertex2f(x  +0,y+200);
		GL11.glEnd();
		
		// Right box
		x = 100;
		y = -100;
		setColor(pX+1, pY, map);
		GL11.glBegin(GL11.GL_QUADS);
			GL11.glVertex2f(x    ,y    );
			GL11.glVertex2f(x+200,y  +0);
			GL11.glVertex2f(x+200,y+200);
			GL11.glVertex2f(x  +0,y+200);
		GL11.glEnd();
		
		
		// Up box
		x = -100;
		y = 100;
		setColor(pX, pY-1, map);
		GL11.glBegin(GL11.GL_QUADS);
			GL11.glVertex2f(x    ,y    );
			GL11.glVertex2f(x+200,y  +0);
			GL11.glVertex2f(x+200,y+200);
			GL11.glVertex2f(x  +0,y+200);
		GL11.glEnd();
		
		// Down box
		x = -100;
		y = -300;
		setColor(pX, pY+1, map);
		GL11.glBegin(GL11.GL_QUADS);
			GL11.glVertex2f(x    ,y    );
			GL11.glVertex2f(x+200,y  +0);
			GL11.glVertex2f(x+200,y+200);
			GL11.glVertex2f(x  +0,y+200);
		GL11.glEnd();
		
		// Top-Left box
		x = -300;
		y = 100;
		setColor(pX-1, pY-1, map);
		GL11.glBegin(GL11.GL_QUADS);
			GL11.glVertex2f(x    ,y    );
			GL11.glVertex2f(x+200,y  +0);
			GL11.glVertex2f(x+200,y+200);
			GL11.glVertex2f(x  +0,y+200);
		GL11.glEnd();
		
		// Top-Right box
		x = 100;
		y = 100;
		setColor(pX+1, pY-1, map);
		GL11.glBegin(GL11.GL_QUADS);
			GL11.glVertex2f(x    ,y    );
			GL11.glVertex2f(x+200,y  +0);
			GL11.glVertex2f(x+200,y+200);
			GL11.glVertex2f(x  +0,y+200);
		GL11.glEnd();
		
		// Bottom-Left box
		x = -300;
		y = -300;
		setColor(pX-1, pY+1, map);
		GL11.glBegin(GL11.GL_QUADS);
			GL11.glVertex2f(x    ,y    );
			GL11.glVertex2f(x+200,y  +0);
			GL11.glVertex2f(x+200,y+200);
			GL11.glVertex2f(x  +0,y+200);
		GL11.glEnd();
		
		// Bottom-Right box
		x = 100;
		y = -300;
		setColor(pX+1, pY+1, map);
		GL11.glBegin(GL11.GL_QUADS);
			GL11.glVertex2f(x    ,y    );
			GL11.glVertex2f(x+200,y  +0);
			GL11.glVertex2f(x+200,y+200);
			GL11.glVertex2f(x  +0,y+200);
		GL11.glEnd();
		
		// Center box
		x = -100;
		y = -100;
		setColor(pX, pY, map);
		GL11.glBegin(GL11.GL_QUADS);
			GL11.glVertex2f(x    ,y    );
			GL11.glVertex2f(x+200,y  +0);
			GL11.glVertex2f(x+200,y+200);
			GL11.glVertex2f(x  +0,y+200);
		GL11.glEnd();
		
		// Player
		x = -50;
		y = -50;
		GL11.glColor3f(1,1,1);
		GL11.glBegin(GL11.GL_QUADS);
			GL11.glVertex2f(x    ,y    );
			GL11.glVertex2f(x+100,y  +0);
			GL11.glVertex2f(x+100,y+100);
			GL11.glVertex2f(x  +0,y+100);
		GL11.glEnd();
	}
	
	/* Function setColor(int x, int y, int [][] tmap)
	 * Returns a fitting color based on what is on the given
	 * coordinates on the given map.
	 */
	private static void setColor(int x, int y, int [][] tmap) {
		if(x<0 || y<0 || x>MAP_WIDTH-1 || y>MAP_HEIGHT-1) {
			GL11.glColor3f(1,0,0);
			return;
		}
		
		switch(tmap[x][y]) {
		case MAP_BLOCK:
			GL11.glColor3f(1,0,0);
			break;
		case MAP_SPACE:
			GL11.glColor3f(0,0,1);
			break;
		case MAP_WIN:
			GL11.glColor3f(0,1,0);
			break;
		}
	}
	
	/* Function checkKeys()
	 * Reads for key input and acts accordingly. More specifically,
	 * the player is moved from arrow key presses.
	 */
	private static void checkKeys() {
		// Check for "Up" key
		if(Keyboard.isKeyDown(Keyboard.KEY_UP) && keyRefresh[DIR_UP]) {
			if(movePlayer(DIR_UP, pX, pY, map)) {
				pY--;
				recActions[currentAction].setAction(DIR_UP);
				currentAction++;
			}
			keyRefresh[DIR_UP] = false;
		} else if(!Keyboard.isKeyDown(Keyboard.KEY_UP)) {
			keyRefresh[DIR_UP] = true;
		}
		// Check for "Down" key
		if(Keyboard.isKeyDown(Keyboard.KEY_DOWN) && keyRefresh[DIR_DOWN]) {
			if(movePlayer(DIR_DOWN, pX, pY, map)) {
				pY++;
				recActions[currentAction].setAction(DIR_DOWN);
				currentAction++;
			}
			keyRefresh[DIR_DOWN] = false;
		} else if(!Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
			keyRefresh[DIR_DOWN] = true;
		}
		// Check for "Left" key
		if(Keyboard.isKeyDown(Keyboard.KEY_LEFT) && keyRefresh[DIR_LEFT]) {
			if(movePlayer(DIR_LEFT, pX, pY, map)) {
				pX--;
				recActions[currentAction].setAction(DIR_LEFT);
				currentAction++;
			}
			keyRefresh[DIR_LEFT] = false;
		} else if(!Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
			keyRefresh[DIR_LEFT] = true;
		}
		// Check for "Right" key
		if(Keyboard.isKeyDown(Keyboard.KEY_RIGHT) && keyRefresh[DIR_RIGHT]) {
			if(movePlayer(DIR_RIGHT, pX, pY, map)) {
				pX++;
				recActions[currentAction].setAction(DIR_RIGHT);
				currentAction++;
			}
			keyRefresh[DIR_RIGHT] = false;
		} else if(!Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
			keyRefresh[DIR_RIGHT] = true;
		}
		// Check for "R" key
		if(Keyboard.isKeyDown(Keyboard.KEY_R) && keyRefresh[5]) {
			keyRefresh[5] = false;
			replay = true;
			pX = MAP_WIDTH/2;
			pY = 0;
		} else if(!Keyboard.isKeyDown(Keyboard.KEY_R)) {
			keyRefresh[5] = true;
		}
	}
	
	/* Function movePlayer(int dir, int x, int y, int [][] tmap)
	 * Checks move requests for validity. Returns true if no
	 * obstructions would keep the player from moving in that direction.
	 */
	private static boolean movePlayer(int dir, int x, int y, int [][] tmap) {
		switch(dir) {
		case DIR_UP:
			if(y>0) {
				if(tmap[x][y-1] != MAP_BLOCK) {
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
			// break;
		case DIR_DOWN:
			if(y<MAP_HEIGHT-1) {
				if(tmap[x][y+1] != MAP_BLOCK) {
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
			// break;
		case DIR_LEFT:
			if(x>0) {
				if(tmap[x-1][y] != MAP_BLOCK) {
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		case DIR_RIGHT:
			if(x<MAP_HEIGHT-1) {
				if(tmap[x+1][y] != MAP_BLOCK) {
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		default:
			System.out.printf("Error: Unexpected direction in movePlayer.\n");
		}
		
		return false;
	}
	
	/* Function makeMaze()
	 * Randomly creates a maze by drawing lines of a random
	 * direction and size and returns a two dimensional
	 * array with the map information.
	 */
	private static int[][] makeMaze() {
		int [][] out = new int [MAP_WIDTH][MAP_HEIGHT];
		for(int x=0; x<MAP_WIDTH; x++) {
			for(int y=0; y<MAP_HEIGHT; y++) {
				out[x][y] = MAP_BLOCK;
			}
		}
		
		int x = MAP_WIDTH/2;
		int y = 0;
		out[x][y] = MAP_SPACE;
		int lastDir = -1;
		for(int i=0; i<20; i++) {
			int dir = generator.nextInt(4);
			int len = generator.nextInt(4);
			while( (dir==0 && lastDir==3) || (dir==1 && lastDir == 2) || (dir==2 && lastDir==1) || (dir==3 && lastDir==0) ) {
				dir = generator.nextInt(4);
			}
			switch (dir) {
			case 0:		//Go down
				for(int j=0; j<len; j++) {
					if(y < MAP_WIDTH-1) {
						y+=1;
						out[x][y] = MAP_SPACE;
					}
				}
				break;
			case 1:		//Go right
				for(int j=0; j<len; j++) {
					if(x < MAP_HEIGHT-1) {
						x+=1;
						out[x][y] = MAP_SPACE;
					}
				}
				break;
			case 2:		//Go left
				for(int j=0; j<len; j++) {
					if(x > 0) {
						x-=1;
						out[x][y] = MAP_SPACE;
					}
				}
				break;
			case 3:		//Go up
				for(int j=0; j<len; j++) {
					if(y>0) {
						y-=1;
						out[x][y] = MAP_SPACE;
					}
				}
				break;
			default:
				System.out.printf("Error: Unexpected random value in map gen. %d\n", dir);
			}
			lastDir = dir;
		}
		
		out[x][y] = MAP_WIN;
		
		return out;
	}
	
	/* Function printMaze(int[][] tmap)
	 * Prints the given map as text.
	 */
	private static void printMaze(int[][] tmap) {
		for(int x=0; x<MAP_WIDTH+2; x++) {
			System.out.printf("[-]");
		}
		System.out.println("");
		for (int y = 0; y < MAP_WIDTH; y++) {
			System.out.printf("[|]");
			for (int x = 0; x < MAP_HEIGHT; x++) {
				switch (tmap[x][y]) {
				case MAP_START:
					System.out.printf(" s ");
				case MAP_BLOCK:
					System.out.printf("[ ]");
					break;
				case MAP_SPACE:
					System.out.printf("   ");
					break;
				case MAP_WIN:
					System.out.printf(" w ");
				}
			}
			System.out.printf("[|]");
			System.out.println("");
		}
		for(int x=0; x<MAP_WIDTH+2; x++) {
			System.out.printf("[-]");
		}
		
		System.out.printf("\n");
	}
}