import java.util.Random;

import etc.Constants;


public class GeneticAlgorithm {

	public static int runs;
	public static int[][][][][][] solution;//[run][in1][in2][in3][in4][lastOutput]
	public static int[] output;
	public static int lastOutput;					//goes into solution[][][][][][lastOutput], and tracks the last move. Is set to -1 until the bot moves.
	public static int[] runFitness;	//gets the fitness for all the runs. [runs]
	public Random random = new Random();			//the # in parenthesis is the seed for the random numbers
	
	public GeneticAlgorithm(int runsCount){			//sets up runs and the input array
		
		runs = runsCount;
		solution = new int[runs][2][2][2][2][4];
		//goes through every cell in the solution array and sets it to -1
		for(int in0 = 0; in0 < runs; in0++){
			for(int in1 = 0; in1 < 2; in1++){
				for(int in2 = 0; in2 < 2; in2++){
					for(int in3 = 0; in3 < 2; in3++){
						for(int in4 = 0; in4 < 2; in4++){
							for(int in5 = 0; in5 < 4; in5++){
								solution[in0][in1][in2][in3][in4][in5] = -1;
							}
						}
					}
				}
			}
		}
		lastOutput = 0;	//set to 0, for the first run.
		runFitness = new int[runs];	//gets the fitness for all the runs. [runs]
	}
	
	/*
	 * must recieve the input set
	 * 
	 *  0 = empty block
	 *  1 = filled block
	 *  
	 * int[] inputs: 
	 * 		[0] = top block
	 * 		[1] = right block
	 * 		[2] = bottom block
	 * 		[3] = left block
	 * will output a number 0-3 
	 * 		0 = up
	 * 		1 = right
	 * 		2 = down
	 * 		3 = left
	 */	
	public int getOutput(int run, int[] inputs){//returns a number between 0 and 3 for the solution set
		
		int out = solution[run][ inputs[0] ][ inputs[1] ][ inputs[2] ][ inputs[3] ][lastOutput];	//for readablity
		
		//if there is no solution for that reaction do the following
		if(out == -1){//|| out == opposite(lastOutput
			
			randomizeSolution(run,inputs);		//randomizes that solution
			out = solution[run][ inputs[0] ][ inputs[1] ][ inputs[2] ][ inputs[3] ][	lastOutput	];	//sets out once again
		}
		
		lastOutput = out;
		return lastOutput;
		
	}

	public void mutate(){			//higher fitness is worse.
		int[] topX = new int[3];					//HOLDS THE RUN NUMBER of the top [x] solutions. 0 is the highest fitness run, x is the lowest fitness run.
		for(int i = 0; i < topX.length; i++){topX[i] = -1;}	//sets all of topX to -1
		
		for(int run = 0; run < runs; run++){		//Finds the topx solutions.
			for(int i = 0; i < topX.length; i++){			//goes through topX array, to see if the current run was more fit than the currently scanned runs in topX
				if(topX[i] == -1 || runFitness[run] < runFitness[topX[i]]){	//if there is a new record for the i'th place, then set it.					
					if(i < topX.length-1){		//if it is still greater than one of the top 3
						topX[i+1] = topX[i];	//moves the top variable up one, to make space for the new one.
					}
					topX[i] = run;				//sets the new topX
					break;
				}
			}	
		}
		//System.out.print("1st: "+runFitness[topX[0]]+" Run: "+topX[0]);				//Prints the top fitness's of the generation, for debugging.
		//System.out.print("		2nd: "+runFitness[topX[1]]+" Run: "+topX[1]);
		//System.out.println("		3rd: "+runFitness[topX[2]]+"Run: "+topX[2]);
		
		int[][][][][][] solutionPlaceholder = new int[runs][2][2][2][2][4];
		System.arraycopy(solution, 0, solutionPlaceholder, 0, solution.length);
		for(int runx = 0; runx < runs; runx++){
			for(int in1 = 0; in1 < 2; in1++){									// format: solution[runx][in1][in2][in3][in4]. Same format for solutionPlaceholde				
				for(int in2 = 0; in2 < 2; in2++){					
					for(int in3 = 0; in3 < 2; in3++){					
						for(int in4 = 0; in4 < 2; in4++){
							for(int in5 = 0; in5 < 4; in5++){
								
								
								if(runx == 0 || runx == 1 || runx == 2){
									solutionPlaceholder[runx][in1][in2][in3][in4][in5] = solution[topX[runx]][in1][in2][in3][in4][in5];
								}else{//if not run 0
									int r = random.nextInt((int) topX.length+5);								
									switch(r){
										case 0:
											solutionPlaceholder[runx][in1][in2][in3][in4][in5] = solution[topX[0]][in1][in2][in3][in4][in5];
											break;
										case 1:
											solutionPlaceholder[runx][in1][in2][in3][in4][in5] = solution[topX[1]][in1][in2][in3][in4][in5];
											break;
										case 2:
											solutionPlaceholder[runx][in1][in2][in3][in4][in5] = solution[topX[2]][in1][in2][in3][in4][in5];
											break;
										case 3:
											solutionPlaceholder[runx][in1][in2][in3][in4][in5] = random.nextInt(4);
											break;
									}
										
									
								}
							}	
						}					
					}
				}				
			}			
		}
		solution = solutionPlaceholder;
	}
	
	public void getFitness(int[] fitness, int run){	//returns an array[runs] that has the fitness for each run.
		int movePunishment = 		-100;			//punishment for amount of moves. movePunishment*moveCount = total punishment.
		int lossPunishment = 		100000;		//punishment for not completing the maze.
		int repeatPunishment = 		50;			//punishment for going over the same square more than once. repeatPunishment*repeats = total punishment
		
		//individually sets each runFitness. This is the main block of code.
		runFitness[run] = 0;							//Resets from previous runfitness's in other generations.	
		if(fitness[1] == 0){							//		Calculates the punishment for losing	//
			runFitness[run] += (fitness[0]-fitness[2])*movePunishment;	//		Calculates the punishment for moving	//
			runFitness[run] += lossPunishment;			//if it lost, remove points.
		}else{
			runFitness[run] += -lossPunishment;			//if it won, add points.
			runFitness[run] += (fitness[0]-fitness[2])*-movePunishment;		//once it has finished the maze, it gives punishment for more moves.
			runFitness[run] += fitness[2]*repeatPunishment;
		}
	}
	
	public static int opposite(int direction){	//returns the opposite direction. If not valid, returns -1.
		
		switch(direction){
			case Constants.DIR_UP: return Constants.DIR_DOWN;
			case Constants.DIR_DOWN:	return Constants.DIR_UP;
			case Constants.DIR_LEFT:	return Constants.DIR_RIGHT;
			case Constants.DIR_RIGHT:	return Constants.DIR_LEFT;
			
		}
		
		return -1;
	}

	
	public void randomizeSolution(int run, int[] inputs){

		solution[run][ inputs[0] ][ inputs[1] ][ inputs[2] ][ inputs[3] ][	lastOutput	] = random.nextInt(4);//sets a random output between 0 and 3 for that input set
	}
	

}
