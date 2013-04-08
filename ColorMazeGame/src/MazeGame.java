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

import Net.ReadSolutions;

import sql.InfoPackage;
import etc.Constants;
import etc.MazeMap;

public class MazeGame {	//0=UP;	1=DOWN;	2=LEFT;	3=RIGHT
	//			Pre-Genetic Algorithm Code				//
	private static Random generator = new Random(5);
	private static int[][] map;							// Universal map array [x left = 0][y, top = 0] Returns a constant for what is in that particular space (MAP_BLOCK,ect.)	
	private static int pX, pY;							// Player x and y (within the map array)
	public static int moveCount = 0;
	//			Variables that you can change			//
	public static int runs = 				30000;		//total runs to train the AI
	public static int frameSpeed = 			250;			//how many miliseconds per frame
	//public static int maxSolutionSize = 	500;		//how long we will allow solutions to be.
	public static int maxRepeatsonBlock = 	3;			//What the max repeats on a block will be, before the AI quits out of the map and retrys.
	public static float percentSolutions = 	.012f;		//What percent of the mapSolutions data points to teach the AI. 1 = 100%
	public static int hiddenLayers = 		12;			//How many hidden layers the ANN will have.
	//			Non Changable Variables 				//
	public static double[] inputs = new double[5];		//how many inputs there are. (shows the blocks in the directions up, down, left, right. to the player NOT IN THAT ORDER)
	public static double[][] inputSet;					//Is filled in by ReadSolution. It holds all the data points (expanded) from the mapSolutions file.
	public static double[][] outputSet;
	public static int[][] mapCount;						//counts how many times the player has been on a particular block in the map. If he has passed the same block +10 times, the run is quit.
	public static NeuralNetwork ai; 					//is set up in the begin method
	public static ReadSolutions r;						//sets up the ReadSolutions to use with AI
	public static boolean ranIntoWall = false;
	public static boolean hasWonGame = false;	
	public static String mapnumber = "map1.txt";		//What map the AI will be learning.
	public static String finalSolution = "";
	public static int lastOutput;
	/* Function main(String args[])
	 * Runs maze creation, sets some variables, and starts
	 * the main loop.
	 */
	public static void main(String args[]) {

		resetMap();		//sets map up.
		
		printMaze(map);	//prints map on console
		//ai = new NeuralNetwork(5,14, 2, .012f, runs, mapnumber);			//Starts and trains the net. Format: (Inputs,Hidden,Outputs,% data to train, max cycles to train, map)
		//System.out.println("Finished Training");
		begin();
	}
	
	/* Function begin()
	 * Sets up OpenGL and lwjgl and contains the main loop.
	 */
	private static void begin() {		
		setUpScreen();		
		
		while(!Display.isCloseRequested()) {	// Start main loop

			resetMap();
			findSolution();
		}			
		//end of while loop, when display closes.
		Display.destroy();
	}
	
	private static void findSolution(){
		
		r = new ReadSolutions(mapnumber);
		inputSet = 	r.getInputs(percentSolutions);										//Finds random inputs, a percent amount of total data. It then sets the output array, to be pulled in getOutputs()
		outputSet = new double[inputSet.length][2];										//Creates new outputSet
		for(int i = 0; i < outputSet.length; i++){										//Clears outputSet
			for(int j = 0; j < outputSet[i].length; j++){
				outputSet[i][j] = 0;
			}
		}
		outputSet = r.getOutputs();														//Finds the corrosponding outputSet to the inputSet
		ai = new NeuralNetwork(5,hiddenLayers, 2, inputSet,outputSet, runs, mapnumber);	//Starts and trains the net. Format: (Inputs,Hidden,Outputs,% data to train, max cycles to train, map)
		
		lastOutput = Constants.DIR_UP;
		finalSolution = "";
		while(!Display.isCloseRequested()) {	
			if(stuckInLoop()){						//If stuck, reset everything and try again.
				System.out.println("Stuck");
				deleteBadInput(findBadInput());
				resetMapCount();
				break;
			}
			if(map[pX][pY] == Constants.MAP_WIN){	//If it has won game, break out of the while loop
				System.out.println(finalSolution);
				System.out.println("Solved");
				sleep(1500);
				break;
			}
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);	// Clears screen and depth buffer	
			render();
			
			setInputs();
			finalSolution += checkKeys();		
			sleep(frameSpeed);
			Display.update();
		}

	}
	
	private static deleteBadInput(int badInput){
		double[][] inputX = new double[inputSet.length-1][inputSet[0].length];			//Makes a copy inputsetArray that is one less large than the original inputSet array
		double[][] outputX = new double[outputSet.length-1][outputSet[0].length];
		
		
		inputSet = 	r.getInputs(percentSolutions);										//Finds random inputs, a percent amount of total data. It then sets the output array, to be pulled in getOutputs()
		outputSet = new double[inputSet.length][2];										//Creates new outputSet
	}
	
	private static int findBadInput(){
		int badInput = -1;										//This will hold the number of the array of inputSet that holds the bad set of inputs, which got the neural network stuck in the maze.
		for(int i = 0; i < inputSet.length; i++){				//Goes through each inputset, and finds the one that the player is currently at. (If it even exists)
			//boolean isOriginal = true;						//is it an original copy? True until proven false.
			int counter = 0;
			for(int j = 0; j < inputSet[i].length; j++){		//checks if the current inputs is equal to the current inputs in the current inputSet
				if(inputSet[i][j] != inputs[j]){break;}			//if it is original, break out of checking.
				if(inputSet[i][j] == inputs[j]){counter += 1;}	//if the counter ends up being equal to inputs.length, then the whole input is a copy, thus it is the input that got the program stuck.		
				
			}
			if(counter == inputs.length){
				badInput = i;
				break;
			}
		}
		
		return badInput;
	}
	
	private static void askNet(){					//Purely for net testing purposes. 
		System.out.println("Test mode activated");
		System.out.println("0,0=up	0,1=down	1,0=left	1,1=right");
		double in0 = readInfo("BLock above: ");
		double in1	= readInfo("Block below: ");
		double in2 = readInfo("Block left: ");
		double in3	= readInfo("Block right: ");
		double in4 = readInfo("Last move: ");
		double[] in = {in0,in1,in2,in3,in4};
		ai.testNet(in);
	}
	
	private static void resetMapCount(){			//Resets JUST the count of the map cells
		for(int x=0; x<Constants.MAP_WIDTH; x++) {
			for(int y=0; y<Constants.MAP_HEIGHT; y++) {
				
				mapCount[x][y] = 0;
			}
		}	
	}
	
	private static void resetMap(){
		map = new int [Constants.MAP_WIDTH][Constants.MAP_HEIGHT];		//sets array to map size
		mapCount = new int[Constants.MAP_WIDTH][Constants.MAP_HEIGHT];	//sets array to map size
		moveCount = 0;
		ranIntoWall = false;
		MazeMap maze = new MazeMap();//loads map from text file
		maze.loadMap(mapnumber);
		
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
	
	private static double readInfo(String prompt){			//Tool for reading lines from console
		
		//System.out.printf(prompt + "\n");//prompt
		System.out.printf(prompt);//prompt
		
		//read information from console
		try{
		    BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
		    String s = bufferRead.readLine();
	 
		    double x = Double.parseDouble(s);
		    return x;
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		return -10000;
	}
	
	private static String checkKeys() {						//Reads for key input and acts accordingly. Player is moved from arrow key presses.
		int action = ai.testNet(inputs);					//returns a number between 0 and 3 for the solution set
		lastOutput = action;
		String move = "";
		if( action == Constants.DIR_UP ) {									//if output says go up
			if(movePlayer(Constants.DIR_UP, pX, pY, map)) {	//checks if you can or can't move into that space
				pY--;
				//recActions[moveCount] = Constants.DIR_UP;
				moveCount++;
				move = "u";
			}else{//if you couldn't move into that spot
				ranIntoWall = true;//If you're running into a wall, it ends the run.
			}
		}
		if( action == Constants.DIR_DOWN ) {//if output says go down
			if(movePlayer(Constants.DIR_DOWN, pX, pY, map)) {
				pY++;
				//recActions[moveCount] = Constants.DIR_DOWN;
				moveCount++;
				move = "d";
			}else{//if you couldn't move into that spot
				ranIntoWall = true;//If you're running into a wall, it ends the run.
			}
		}
		if( action == Constants.DIR_LEFT ) {//if output says go left
			if(movePlayer(Constants.DIR_LEFT, pX, pY, map)) {
				pX--;
				//recActions[moveCount] = Constants.DIR_LEFT;
				moveCount++;
				move = "l";
			}else{//if you couldn't move into that spot
				ranIntoWall = true;//If you're running into a wall, it ends the run.
			}
		}
		if( action == Constants.DIR_RIGHT ) {//if output says go right
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
		//Order: [0] = up, [1] = down, [2] = left, [3] = right, [4] = lastOutput
		if(movePlayer(Constants.DIR_UP, pX, pY, map)){//if there is an open space above you
			inputs[0] = Constants.MAP_SPACE;//0 = up
		}else{//if there's not an open space above you
			inputs[0] = Constants.MAP_BLOCK;//1 = down
		}
		
		if(movePlayer(Constants.DIR_DOWN, pX, pY, map)){//if there's an open spot below you
			inputs[1] = Constants.MAP_SPACE;//1 = down
		}else{
			inputs[1] = Constants.MAP_BLOCK;//1 = down
		}
		
		if(movePlayer(Constants.DIR_LEFT, pX, pY, map)){//if there's an open spot left of you
			inputs[2] = Constants.MAP_SPACE;//2 = left
		}else{
			inputs[2] = Constants.MAP_BLOCK;//2 = left
		}
		
		if(movePlayer(Constants.DIR_RIGHT, pX, pY, map)){//if there's an open spot right of you
			inputs[3] = Constants.MAP_SPACE;//3 = right
		}else{
			inputs[3] = Constants.MAP_BLOCK;//3 = right
		}
		inputs[4] = lastOutput;
	}
	
	private static boolean stuckInLoop(){			//returns true if the AI has passed the same block more than maxRepeatsonBlock times
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