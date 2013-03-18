/* MazeGame Class
 * By Tyler Compton for Team Tyro
 * 
 * This is a very simple and minimal map game. It's official name is
 * "Color Maze Game."
 */



import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

import sql.InfoPackage;
import etc.Constants;
import etc.MazeMap;

public class MazeGame {
	//			Pre-Genetic Algorithm Code				//
	private static Random generator = new Random();
	private static int[][] map;				// Universal map array [x left = 0][y, top = 0] Returns a constant for what is in that particular space (MAP_BLOCK,ect.)	
	private static int [] recActions; 		// Stores all the keys pressed. [DIR_RIGHT,UP,DOWN,LEFT]. CURRENTLY NOT USED, WILL IMPLEMENT WHEN IT IS THE FINAL RUN OF FINAL GENERATION.
	private static int moveCount; 			// Keeps track of which part of recActions your using. Basically just a counter for recActions
	private static int rmoveCount;			// Replay current action, just for replaying
	private static java.util.Date startDate, endDate; // Actual day, time, milliseconds that you played the game.	
	private static int pX, pY;				// Player x and y (within the map array)

	//			Variables that you can change			//
	public static int runs = 				50;			//total runs
	//public static int generations = 		10;			//total generations
	public static int frameSpeed = 			25;			//how many miliseconds per frame
	public static int maxSolutionSize = 	500;		//how long we will allow solutions to be.
	public static int maxRepeatsonBlock =	10;			//the max amount of time an AI is allowed to repeat on a block, before it quits out.
	
	//			Non Changable Variables 				//
	public static boolean ranIntoWall = false;
	public static boolean hasWonGame = false;
	public static int run = 0;				//keeps track of the current run
	public static int generation =	0;		//keeps track of the current generation
	public static int[] inputs = new int[4];//how many inputs there are. (shows the blocks in the directions up, down, left, right. to the player NOT IN THAT ORDER)
	public static int[] fitness;			//[run][aspect of run] records aspects of each run. [run][0=total moves, 1=how it won, 2=most repeated space] more documentation on ai.mutate() in class GA
	public static int[][] mapCount;			//counts how many times the player has been on a particular block in the map. If he has passed the same block +10 times, the run is quit.
	public static GeneticAlgorithm ai; 		//is set up in the begin method
	
	
	/* Function main(String args[])
	 * Runs maze creation, sets some variables, and starts
	 * the main loop.
	 */
	public static void main(String args[]) {
		map = new int [Constants.MAP_WIDTH][Constants.MAP_HEIGHT];		//sets array to map size
		mapCount = new int[Constants.MAP_WIDTH][Constants.MAP_HEIGHT];	//sets array to map size
		fitness = new int[4];
		recActions = new int [500];
		startDate = new java.util.Date();
		
		resetMap();		//sets map up.
		
		printMaze(map);	//prints map on console
		
		begin();
	}
	
	/* Function begin()
	 * Sets up OpenGL and lwjgl and contains the main loop.
	 */
	private static void begin() {		
		setUpScreen();		
		ai = new GeneticAlgorithm(runs);
		String mapSolution = new String(learnMap());
		System.out.println(mapSolution);
		
		while(!Display.isCloseRequested()) {	// Start main loop
			int currentMove = 0;
			resetMap();	
			while(!Display.isCloseRequested() && map[pX][pY] != Constants.MAP_WIN){	//While showing the recorded solution, and it has not finished yet.
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);	// Clears screen and depth buffer	
			render();	
			
			replayGame(mapSolution, currentMove);	
			currentMove += 1;
			sleep(frameSpeed);
			Display.update();
				if(Display.isCloseRequested()){
					Display.destroy();
				}
			}
			
			sleep(frameSpeed*10);	//Shows it at the endpoint for a little bit.
		}			
		//end of while loop, when display closes.
		Display.destroy();
	}
	
	private static String learnMap(){
		
		while(map[pX][pY] != Constants.MAP_WIN){//While you have not finished fully solving the maze.
			if(run < runs && continueRun()) {	//if you have not won yet
				setInputs();
				checkKeys(run);					//updates the inputs[] array to the current screen
				
			}else{								//Once the run has been won	
				if(run < runs){					//if you still have more runs to complete
					recordFitness();			//records how fit the last run was (how good it did) used to send to the ai.mutate() algorithm.
					ai.getFitness(fitness, run);//sends ai the recently recorded fitness.
					//if(generation % 100 == 0 && run == 0){System.out.println("Repeats: "+fitness[2]+"	Total Moves: "+(fitness[0]-fitness[2])+"	Fitness: "+ai.runFitness[run]);}
					run = run+1;				//move to the next run
					resetMap();					//resets map for the next run.
					
				}else{							//if you have won and all runs are complete. Resets and mutates solution sets for the next generation. 
					ai.mutate();				//sends fitness to the GA, which will do with it what it wants.
					run = 0;
					resetMap();					//resets map for the next run.
					generation++;
				}
			} 
		}
		
		String mapSolution = "";
		resetMap();								//The maze has now been fully solved. Reset map, replay- but this time it will be recorded into a string.
		while(map[pX][pY] != Constants.MAP_WIN){//Replay game. Record this time.
			setInputs();
			mapSolution = mapSolution.concat(checkKeys(run));
		}
		return mapSolution;
	}
	private static void resetMap(){
		
		ranIntoWall = false;
		moveCount = 0;
		ai.lastOutput = 0;
		MazeMap maze = new MazeMap();//loads map from text file
		maze.loadMap("map1.txt");
		
		for(int x=0; x<Constants.MAP_WIDTH; x++) {
			for(int y=0; y<Constants.MAP_HEIGHT; y++) {
				map[x][y] = maze.getSpace(x,y);
				mapCount[x][y] = 0;
				if(map[x][y] == Constants.MAP_START) {
					pX = x;
					pY = y;
				}
			}
		}	
	}
	
	private static int readInfo(String prompt){		//Tool for reading lines from console
		
		//System.out.printf(prompt + "\n");//prompt
		System.out.printf(prompt);//prompt
		
		//read information from console
		try{
		    BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
		    String s = bufferRead.readLine();
	 
		    int x = Integer.parseInt(s);
		    return x;
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		return -10000;
	}
	
	private static String checkKeys(int run) {		//Reads for key input and acts accordingly. Player is moved from arrow key presses.
		int action = ai.getOutput(run, inputs);
		String move = "";
		if( action == 0 ) {//if output says go up
			if(movePlayer(Constants.DIR_UP, pX, pY, map)) {//checks if you can or can't move into that space
				pY--;
				//recActions[moveCount] = Constants.DIR_UP;
				moveCount++;
				move = "u";
			}else{//if you couldn't move into that spot
				ranIntoWall = true;//If you're running into a wall, it ends the run.
			}
		}
		if( action == 1 ) {//if output says go down
			if(movePlayer(Constants.DIR_DOWN, pX, pY, map)) {
				pY++;
				//recActions[moveCount] = Constants.DIR_DOWN;
				moveCount++;
				move = "d";
			}else{//if you couldn't move into that spot
				ranIntoWall = true;//If you're running into a wall, it ends the run.
			}
		}
		if( action == 2 ) {//if output says go left
			if(movePlayer(Constants.DIR_LEFT, pX, pY, map)) {
				pX--;
				//recActions[moveCount] = Constants.DIR_LEFT;
				moveCount++;
				move = "l";
			}else{//if you couldn't move into that spot
				ranIntoWall = true;//If you're running into a wall, it ends the run.
			}
		}
		if( action == 3 ) {//if output says go right
			if(movePlayer(Constants.DIR_RIGHT, pX, pY, map)) {
				pX++;
				//recActions[moveCount] = Constants.DIR_RIGHT;
				moveCount++;
				move = "r";
			}else{//if you couldn't move into that spot
				ranIntoWall = true;//If you're running into a wall, it ends the run.
			}
		}
		mapCount[pX][pY] = mapCount[pX][pY]+1;
		return move;
		
	}
	
	private static void setInputs(){				//0 = open block, 1 = closed block
		
		if(movePlayer(Constants.DIR_UP, pX, pY, map)){//if there is an open space above you
			inputs[0] = 0;//0 = up
		}else{//if there's not an open space above you
			inputs[0] = 1;//1 = down
		}
		
		if(movePlayer(Constants.DIR_DOWN, pX, pY, map)){//if there's an open spot below you
			inputs[1] = 0;//1 = down
		}else{
			inputs[1] = 1;//1 = down
		}
		
		if(movePlayer(Constants.DIR_LEFT, pX, pY, map)){//if there's an open spot left of you
			inputs[2] = 0;//2 = left
		}else{
			inputs[2] = 1;//2 = left
		}
		
		if(movePlayer(Constants.DIR_RIGHT, pX, pY, map)){//if there's an open spot right of you
			inputs[3] = 0;//3 = right
		}else{
			inputs[3] = 1;//3 = right
		}
	}

	private static boolean continueRun(){
		
		if(map[pX][pY] == Constants.MAP_WIN){
			//System.out.print("RUN: "+run+"	Generation: "+generation+"	Reason: Won		"+"Moves: "+ moveCount+"	");
			hasWonGame = true;
			return false;
			
		}
		
		if(stuckInLoop()){
			//System.out.print("RUN: "+run+"	Generation: "+generation+"	Reason: Stuck	"+"Moves: "+ moveCount+"	");
			return false;
		}
		
		if(ranIntoWall){
			//System.out.print("RUN: "+run+"	Generation: "+generation+"	Reason: Wall	"+"Moves: "+ moveCount+"	");
			return false;
		}
		
		if(moveCount >= maxSolutionSize){
			//System.out.print("RUN: "+run+"	Generation: "+generation+"	Reason: MaxMoves"+"Moves: "+ moveCount+"	");
			return false;
		}
		
		return true;
	}
	
	private static boolean stuckInLoop(){			//returns true if the AI has passed the same block more than 10 times
		if(mapCount[pX][pY] > maxRepeatsonBlock){
			return true;
		}else{
			return false;
		}
	}
	
	private static void sleep(int time){			//stops the frame for 'time' miliseconds. 
		
		try {			//slows down the thread
			Thread.sleep(time);
		} catch(InterruptedException ex) {
			Thread.currentThread().interrupt();
		}
		
	}
	
	private static void recordFitness(){			//records how fit the last run was (how good it did) used to send to the ai.mutate() algorithm.
		//[][0] = total moves; [][1] = how the run ended; [][2] = what the most repeated space was. [3]- 1 = it ran into a wall, 0 = it did not run into a wall.
		fitness[0] = moveCount;
			
		if(map[pX][pY] == Constants.MAP_WIN){		//if the solution won the maze! :D
			fitness[1] = 1;
		}else{
			fitness[1] = 0;					//if the solution did not win the maze
		}
		
		int repeats = 0;							//keeps track of the total amounts of repeated blocks.
		for(int x=0; x<Constants.MAP_WIDTH; x++) {			//goes through each map block to find the most traversed block
			for(int y=0; y<Constants.MAP_HEIGHT; y++) {		
				if(mapCount[x][y] != 0){
					repeats += mapCount[x][y]-1;	//sets the new int for most repeats on a block
				}
			}
		}
		fitness[2] = repeats;
		if(ranIntoWall){
			fitness[3] = 1;
		}else{
			fitness[3] = 0;
		}
		//System.out.println("moveCount: "+fitness[0]+"	Win or not: "+fitness[1]+"	Most repeated block: "+fitness[2]+" ");
	}
	
	
	
	
	
	private static void replayGame(String mapSolution, int currAction) {
		switch(mapSolution.charAt(currAction)) {
		case 'd':
			pY++;
			break;
		case 'u':
			pY--;
			break;
		case 'r':
			pX++;
			break;
		case 'l':
			pX--;
			break;
		}
	}
	
	private static void setUpScreen(){
		try {
			Display.setDisplayMode(new DisplayMode(600,600));
			Display.create();
		} catch (LWJGLException e) {
			e.printStackTrace();
			System.exit(0);
		}
		
		// Init OpenGLff
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(-300, 300, -300, 300, 1, -1);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		
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
		if(x<0 || y<0 || x>Constants.MAP_WIDTH-1 || y>Constants.MAP_HEIGHT-1) {
			GL11.glColor3f(1,0,0);
			return;
		}
		
		switch(tmap[x][y]) {
		case Constants.MAP_BLOCK:
			GL11.glColor3f(1,0,0);
			break;
		case Constants.MAP_SPACE:
			GL11.glColor3f(0,0,1);
			break;
		case Constants.MAP_WIN:
			GL11.glColor3f(0,1,0);
			break;
		case Constants.MAP_START:
			GL11.glColor3f(1,1,0);
			break;
		}
	}
	
	/* Function movePlayer(int dir, int x, int y, int [][] tmap)
	 * Checks move requests for validity. Returns true if no
	 * obstructions would keep the player from moving in that direction.
	 */
	private static boolean movePlayer(int dir, int x, int y, int [][] tmap) {
		switch(dir) {
		case Constants.DIR_UP:
			if(y>0) {
				if(tmap[x][y-1] != Constants.MAP_BLOCK) {
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
			// break;
		case Constants.DIR_DOWN:
			if(y<Constants.MAP_HEIGHT-1) {
				if(tmap[x][y+1] != Constants.MAP_BLOCK) {
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
			// break;
		case Constants.DIR_LEFT:
			if(x>0) {
				if(tmap[x-1][y] != Constants.MAP_BLOCK) {
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		case Constants.DIR_RIGHT:
			if(x<Constants.MAP_HEIGHT-1) {
				if(tmap[x+1][y] != Constants.MAP_BLOCK) {
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
	
	/* Method InfoPackage packUp(java.util.Date sD, java.util.Date eD, int[] a)
	 * sD startDate
	 * eD endDate
	 * a=recActions*
	 */
	private static InfoPackage packUp(java.util.Date sD, java.util.Date eD, int[] a) {
		InfoPackage out = new InfoPackage();
		
		out.setDates(sD, eD);
		out.setActions(a);
		
		return out;
	}
	
	/*Work in progress
	 * 
	 * */
	private static boolean sendData(InfoPackage d) {
		boolean success = false;
		
		return success;
	}
	
	/* Function makeMaze()					FOR TESTING
	 * Randomly creates a maze by drawing lines of a random
	 * direction and size and returns a two dimensional
	 * array with the map information.
	 */
	private static int[][] makeMaze() {
		int [][] out = new int [Constants.MAP_WIDTH][Constants.MAP_HEIGHT];
		for(int x=0; x<Constants.MAP_WIDTH; x++) {
			for(int y=0; y<Constants.MAP_HEIGHT; y++) {
				out[x][y] = Constants.MAP_BLOCK;
			}
		}
		
		int x = Constants.MAP_WIDTH/2;
		int y = 0;
		out[x][y] = Constants.MAP_SPACE;
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
					if(y < Constants.MAP_WIDTH-1) {
						y+=1;
						out[x][y] = Constants.MAP_SPACE;
					}
				}
				break;
			case 1:		//Go right
				for(int j=0; j<len; j++) {
					if(x < Constants.MAP_HEIGHT-1) {
						x+=1;
						out[x][y] = Constants.MAP_SPACE;
					}
				}
				break;
			case 2:		//Go left
				for(int j=0; j<len; j++) {
					if(x > 0) {
						x-=1;
						out[x][y] = Constants.MAP_SPACE;
					}
				}
				break;
			case 3:		//Go up
				for(int j=0; j<len; j++) {
					if(y>0) {
						y-=1;
						out[x][y] = Constants.MAP_SPACE;
					}
				}
				break;
			default:
				System.out.printf("Error: Unexpected random value in map gen. %d\n", dir);
			}
			lastDir = dir;
		}
		
		out[x][y] = Constants.MAP_WIN;
		
		return out;
	}
	
	/* Function printMaze(int[][] tmap)		FOR TESTING
	 * Prints the given map as text.
	 */
	private static void printMaze(int[][] tmap) {
		for(int x=0; x<Constants.MAP_WIDTH+2; x++) {
			System.out.printf("[-]");
		}
		System.out.println("");
		for (int y = 0; y < Constants.MAP_WIDTH; y++) {
			System.out.printf("[|]");
			for (int x = 0; x < Constants.MAP_HEIGHT; x++) {
				switch (tmap[x][y]) {
				case Constants.MAP_START:
					System.out.printf(" s ");
				case Constants.MAP_BLOCK:
					System.out.printf("[ ]");
					break;
				case Constants.MAP_SPACE:
					System.out.printf("   ");
					break;
				case Constants.MAP_WIN:
					System.out.printf(" w ");
				}
			}
			System.out.printf("[|]");
			System.out.println("");
		}
		for(int x=0; x<Constants.MAP_WIDTH+2; x++) {
			System.out.printf("[-]");
		}
		
		System.out.printf("\n");
	}
}