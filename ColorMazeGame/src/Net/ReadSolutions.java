package Net;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

import etc.Constants;
import etc.MazeMap;

public class ReadSolutions {
	
	public static String[] solutions;	//Holds a string of each solution
	public static String file = "mapsolutions.txt";
	public static double outputs[][];
	public static int totalMoves;
	public static int solutionsCount;
	public static MazeMap m = new MazeMap();
	public static int[][] map;
	public Random r = new Random(5);//3
	public static String mapnumber;
	public static int sX;				//The x start position of the player
	public static int sY;				//The y start position of the player
	public static Constants constant = new Constants();
	
	public ReadSolutions(String mapx){
		mapnumber = mapx;											//the name of the map file.
		map = new int[Constants.MAP_WIDTH][Constants.MAP_HEIGHT];
		getmapArray();
		System.out.println("////SOLUTION READER////");
		solutions = readSolutions();	//Sets the solutions array to all of the data in the text file. solutions[solution#] = String of solution
		
	}
	
	public static String[] readSolutions(){
		solutionsCount = 0;									//How many solutions there are.
		totalMoves = 0;									//Total amount of moves from each solution added up.
	
		try {												//Gets the amount of solutions, so that I can make an array and record them all as a String.
			BufferedReader in = new BufferedReader(new FileReader(file));
			String line = "";
			while ((line = in.readLine()) != null) {		//If there is still a line, then add to int solutions.
				totalMoves += line.length();
				solutionsCount += 1;
			}
			in.close();
			System.out.println("	Total Moves: "+totalMoves+"	Total Solutions: "+solutionsCount);	
		} catch(IOException ex) {
			System.out.printf("ERROR: Couldn't load map\n");
		}
		
		String[] textSolutions = new String[solutionsCount];		//Now that we know the total solutions, create an array of that size. 
		
		try {												//Records the solutions as String onto the solutions[] array.
			
			BufferedReader in = new BufferedReader(new FileReader(file));
			for(int s = 0; s < solutionsCount; s++) {			//Goes through each line, and records down the solutions in order into the textSolutions array.
				//System.out.println(line);
				textSolutions[s] = in.readLine();
			}
			
			in.close();
		} catch(IOException ex) {
			System.out.printf("ERROR: Couldn't load map\n");
		}
			
/*		for(int s = 0; s < solutions; s++){					//For testing, print out each solution in the solution array.
			System.out.println(textSolutions[s]);
		}*/
		System.out.println("///////////////////////"+"\n");	//The \n will skip to a line.
		return textSolutions;
	}

	public double[][] getInputs(float percent){											//inputs[][] = {	{bUp, bDown, bLeft, bRight, lMov }, {bUp, bDown, bLeft, bRight, lMov }	} an example of an array with two input sets
		int solutionsToRecord = ((int) (percent*totalMoves));
		double inputs[][] = new double[solutionsToRecord][5];							//Makes an array of the appropriate size. (the percent amount of total moves, in int format)
		outputs = new double[solutionsToRecord][2];
		int solutionsRecorded = 0;														//Since the ANN must be fed truly random info, it will just randomly set info until the array is full. This keeps track of how much info has, indeed, been recorded so far.
		System.out.println("	Inputs to learn: "+inputs.length);						//This prints the amount of input sets that will be fed into the ANN.
		
		for(int i =0; i< inputs.length; i++){
			for(int j=0; j < inputs[i].length; j++){
				inputs[i][j] = -10;
			}
		}
		
		
		while(solutionsRecorded < inputs.length){										//While the array has not been fully filled.
			for(int s = 0; s < solutions.length; s++){									//Goes through each move of each solution, picking at random (percent) intervals an input to record.
				for(int l = 0; l < solutions[s].length(); l++){							//Goes through the entire string of that particular solution.
					
					if(r.nextFloat() < percent && solutionsRecorded < inputs.length){	//If it randomly chooses it, then record that input.
						//System.out.println(""+solutions[s].charAt(l));
						double[] test = getSituation(solutions[s], l);
						boolean isOriginal = true;										//Holds wether or not the new solution datapoint is a new, original, datapoint.
						for(int i = 0; i < inputs.length; i++){							//Goes through each input set, and checks that there has not been an equivalent marked down yet.
							int copy = 0;
							for(int j = 0; j < inputs[i].length; j++){					//Goes through each set of inputs from an input set
								if(inputs[i][j] != test[j]){
									break;
								}
								if(inputs[i][j] == test[j]){
									copy += 1;
								}

							}
							if(copy == inputs[i].length){
								isOriginal = false;
								break;
							}
						}
						if(isOriginal){
							inputs[solutionsRecorded] = getSituation(solutions[s], l);		//Gets the inputs at the time that that situation was recorded.
							outputs[solutionsRecorded] = getOutputNumber(solutions[s].charAt(l));		//Records the given output (move) for that situation in the solution string.
							solutionsRecorded += 1;
						}
						
						break;
					}
				}
			}
			
		}
		System.out.println("	Inputs to learn: "+inputs.length);						//This prints the amount of input sets that will be fed into the ANN.
		
		return inputs;
	}
	
	public double[] getSituation(String solution, int move){							//Gets the inputs at the time that a particular move was performed, in the String solution.
		
		double[] situation = new double[5];	//Order: [0] = up, [1] = down, [2] = left, [3] = right, [4] = lastOutput. 0 = open block, 1 = filled block.
		
		int rightMoves = 0;
		int leftMoves = 0;
		int upMoves = 0;
		int downMoves = 0;
		
		for(int m = 0; m < move; m++){
			if(solution.charAt(m) == 'r'){	rightMoves += 1;}
			if(solution.charAt(m) == 'l'){	leftMoves += 1;	}
			if(solution.charAt(m) == 'u'){	upMoves += 1;	}
			if(solution.charAt(m) == 'd'){	downMoves += 1;	}
		}
		int pX = sX + (rightMoves - leftMoves);	//finds the player position at that time.
		int pY = sY + (downMoves  - upMoves	);	//Finds the player position at that time.
		
		if(pY + 1 < Constants.MAP_HEIGHT){	//If you're not at the bottom of the map.
			situation[1] = map[pX][pY+1];	//below you
		}else{ situation[1] = Constants.MAP_BLOCK;	}	
		
		if(pY - 1 > 0){						//If you're not at the top of the map.
			situation[0] = map[pX][pY-1];	//above you
			if(situation[0] == Constants.MAP_START){ situation[0] = Constants.MAP_SPACE; }
		}else{	situation[0] = Constants.MAP_BLOCK;}	
		
		if(pX + 1 < Constants.MAP_WIDTH){	//If you're not at the right edge of the map.
			situation[3] = map[pX+1][pY];	//right of you
		}else{	situation[3] = Constants.MAP_BLOCK;}	
		
		if(pX - 1 > 0){						//If you're not at the left edge of the map.	
			situation[2] = map[pX-1][pY];	//left of you
			//if(situation[2] == Constants.MAP_WIN){ situation[2] = Constants.MAP_SPACE; }
		}else{	situation[2] = Constants.MAP_BLOCK;}	
		

		
		if(move > 0){								//Finds the last move. Is recorded as: NO LAST MOVE = 0; 0 =u; 1/3=d; 2/3=l; 1=r
			situation[4] = solution.charAt(move-1);
			if(solution.charAt(move-1) == 'u'){	situation[4] = Constants.DIR_UP;}
			if(solution.charAt(move-1) == 'd'){	situation[4] = Constants.DIR_DOWN;}
			if(solution.charAt(move-1) == 'l'){	situation[4] = Constants.DIR_LEFT;}
			if(solution.charAt(move-1) == 'r'){	situation[4] = Constants.DIR_RIGHT;}
		}else{
			situation[4] = Constants.DIR_UP;
		}
		
		//System.out.println("Solution: " +solution);
		//System.out.println("Move: "+move+" "+solution.charAt(move)+" "+"	Up: "+situation[0]+"	Down: "+situation[1]+"	Left: "+situation[2]+"	Right: "+situation[3]+"	LastMove: "+situation[4]);
		//System.out.println("pXpY("+pX+","+pY+")"+"	pXpY("+sX+","+sY+")");
		return situation;
	}
	
	public double[][] getOutputs(){			//The output array is set inside of the getInputs(percent) method.
		return outputs;
	}
	
	public double[] getOutputNumber(char outputnumber){	//Gets the number version of the output letter.		0,0=u;	0,1=ed;	1,0=l;	1,1=r
		double[] out = new double[2];
		
		if(outputnumber == 'u'){//11 = u; 00 = d; 10 = l; 01 = r
			out[0] = Constants.positive;
			out[1] = Constants.positive;
		}
		if(outputnumber == 'd'){
			out[0] = Constants.negative;
			out[1] = Constants.negative;
		}
		if(outputnumber == 'l'){
			out[0] = Constants.positive;
			out[1] = Constants.negative;
		}
		if(outputnumber == 'r'){
			out[0] = Constants.negative;
			out[1] = Constants.positive;
		}
		
		return out;
		
	}
	
	public static void getmapArray(){		//Sets the map[][] array to the same map[][] array in the main MazeGame.
		m.loadMap(mapnumber);	//loads the map.
		for(int x=0; x<Constants.MAP_WIDTH; x++) {		
			for(int y=0; y<Constants.MAP_HEIGHT; y++) {
				map[x][y] = m.getSpace(x,y);
				if(map[x][y] == Constants.MAP_START) {
					sX = x;
					sY = y;
					System.out.println("Player X: "+sX+"	Player Y: "+sY);
				}
			}
		}	
		
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
}
