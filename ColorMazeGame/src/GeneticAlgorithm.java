import java.util.Random;


public class GeneticAlgorithm {

	public static int runs;
	public static int[][][][][] solution;
	public static int[] output;
	public static int lastOutput;				//goes into solution[][][][][][lastOutput], and tracks the last move. Is set to -1 until the bot moves.
	public Random random = new Random();		//the # in parenthesis is the seed for the random numbers
	
	public GeneticAlgorithm(int runsCount){		//sets up runs and the input array
		
		runs = runsCount;
		solution = new int[runs][2][2][2][2];
		lastOutput = -1;
		//goes through every cell in the solution array and sets it to -1
		for(int in0 = 0; in0 < runs; in0++){
			for(int in1 = 0; in1 < 2; in1++){
				for(int in2 = 0; in2 < 2; in2++){
					for(int in3 = 0; in3 < 2; in3++){
						for(int in4 = 0; in4 < 2; in4++){
							solution[in0][in1][in2][in3][in4] = -1;
						}
					}
				}
			}
		}
	}
	
	/***
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
	 **/	
	public int getOutput(int run, int[] inputs){//returns a number between 0 and 3 for the solution set
		
		int out = solution[run][ inputs[0] ][ inputs[1] ][ inputs[2] ][ inputs[3] ];	//for readablity
		
		//if there is no solution for that reaction do the following
		if(out == -1 || out == opposite(lastOutput)){
			
			randomizeSolution(run,inputs);		//randomizes that solution
			out = solution[run][ inputs[0] ][ inputs[1] ][ inputs[2] ][ inputs[3] ];	//sets out once again
		}
		
		lastOutput = out;
		return lastOutput;
		
	}
	
	public static int opposite(int direction){	//returns the opposite direction. If not valid, returns -1.
		
		switch(direction){
			case 0: return 1;
			case 1:	return 0;
			case 2:	return 3;
			case 3:	return 2;
			
		}
		
		return -1;
	}
	
public void randomizeSolution(int run, int[] inputs){

		solution[run][ inputs[0] ][ inputs[1] ][ inputs[2] ][ inputs[3] ] = random.nextInt(3);//sets a random output between 0 and 3 for that input set
	}


}
