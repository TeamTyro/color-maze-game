import java.util.*;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

public class MazeGame {

	private static final int MAP_START = 4;
	private static final int MAP_SPACE = 1;
	private static final int MAP_BLOCK = 2;
	private static final int MAP_WIN = 3;
	private static final int MAP_SIZE = 64;
	private static final int MAP_WIDTH = 16;
	private static final int MAP_HEIGHT = 16;

	private static Random generator = new Random();
	private static int[][] map;

	// main() Whatever, who cares
	public static void main(String args[]) {
		System.out.println("I HAVE NO IDEA WHAT I'M DOING.");
		map = makeMaze();
		printMaze(map);
		
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
		
		while(Display.isCloseRequested()) {
			
			// Rendering
			
			Display.update();
		}
		
		Display.destroy();
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