import java.util.*;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

public class MazeGame {
	
	private static final int DIR_RIGHT = 1;
	private static final int DIR_LEFT = 2;
	private static final int DIR_UP = 3;
	private static final int DIR_DOWN = 4;
	
	private static final int MAP_START = 4;
	private static final int MAP_SPACE = 1;
	private static final int MAP_BLOCK = 2;
	private static final int MAP_WIN = 3;
	
	private static final int MAP_SIZE = 64;
	private static final int MAP_WIDTH = 16;
	private static final int MAP_HEIGHT = 16;

	private static Random generator = new Random();
	private static int[][] map;
	
	private static boolean [] keyRefresh;
	
	private static int pX, pY;

	// main() Whatever, who cares
	public static void main(String args[]) {
		System.out.println("I HAVE NO IDEA WHAT I'M DOING.");
		map = makeMaze();
		printMaze(map);
		
		pX = MAP_WIDTH/2;
		pY = 0;
		keyRefresh = new boolean [5];
		
		begin();
	}
	
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
		
		// Start graphical loop
		while(!Display.isCloseRequested()) {
			// Clears screen and depth buffer
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT); 
			
			// Rendering
			render();
			checkKeys();
			
			Display.update();
		}
		
		Display.destroy();
	}
	
	private static void render() {
		int x, y;
		
		x = -300;
		y = -100;
		GL11.glColor3f(1, 0, 0);
		GL11.glBegin(GL11.GL_QUADS);
			GL11.glVertex2f(x    ,y    );
			GL11.glVertex2f(x+200,y  +0);
			GL11.glVertex2f(x+200,y+200);
			GL11.glVertex2f(x  +0,y+200);
		GL11.glEnd();
		
		x = 100;
		y = -100;
		GL11.glColor3f(1, 0, 0);
		GL11.glBegin(GL11.GL_QUADS);
			GL11.glVertex2f(x    ,y    );
			GL11.glVertex2f(x+200,y  +0);
			GL11.glVertex2f(x+200,y+200);
			GL11.glVertex2f(x  +0,y+200);
		GL11.glEnd();
		
		x = -100;
		y = 100;
		GL11.glColor3f(1, 0, 0);
		GL11.glBegin(GL11.GL_QUADS);
			GL11.glVertex2f(x    ,y    );
			GL11.glVertex2f(x+200,y  +0);
			GL11.glVertex2f(x+200,y+200);
			GL11.glVertex2f(x  +0,y+200);
		GL11.glEnd();
		
		x = -100;
		y = -300;
		GL11.glColor3f(1, 0, 0);
		GL11.glBegin(GL11.GL_QUADS);
			GL11.glVertex2f(x    ,y    );
			GL11.glVertex2f(x+200,y  +0);
			GL11.glVertex2f(x+200,y+200);
			GL11.glVertex2f(x  +0,y+200);
		GL11.glEnd();
		
		x = -50;
		y = -50;
		GL11.glColor3f(1, 1, 1);
		GL11.glBegin(GL11.GL_QUADS);
			GL11.glVertex2f(x    ,y    );
			GL11.glVertex2f(x+100,y  +0);
			GL11.glVertex2f(x+100,y+100);
			GL11.glVertex2f(x  +0,y+100);
		GL11.glEnd();
	}
	
	private static void checkKeys() {
		if(Keyboard.isKeyDown(Keyboard.KEY_UP) && keyRefresh[DIR_UP]) {
			System.out.printf("Up!\n");
			keyRefresh[DIR_UP] = false;
		} else if(!Keyboard.isKeyDown(Keyboard.KEY_UP)) {
			keyRefresh[DIR_UP] = true;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_DOWN) && keyRefresh[DIR_DOWN]) {
			System.out.printf("Down!\n");
			keyRefresh[DIR_DOWN] = false;
		} else if(!Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
			keyRefresh[DIR_DOWN] = true;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_LEFT) && keyRefresh[DIR_LEFT]) {
			System.out.printf("Left!\n");
			keyRefresh[DIR_LEFT] = false;
		} else if(!Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
			keyRefresh[DIR_LEFT] = true;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_RIGHT) && keyRefresh[DIR_RIGHT]) {
			System.out.printf("Right!\n");
			keyRefresh[DIR_RIGHT] = false;
		} else if(!Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
			keyRefresh[DIR_RIGHT] = true;
		}
	}

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
				System.out.printf("OH DAMN! %d\n", dir);
			}
			lastDir = dir;
		}
		
		out[x][y] = MAP_WIN;
		
		return out;
	}

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
	}
}