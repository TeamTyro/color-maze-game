import java.util.Random;


public class GeneticAlgorithm {

	public static int runs;
	public static int[][][][][] solution;
	public static int[] output;
	public Random random = new Random();//the # in parenthesis is the seed for the random numbers
	
	public GeneticAlgorithm(int runsCount){//sets up runs and the input array
		runs = runsCount;
		solution = new int[runs][2][2][2][2];
		//goes through every cell in the solution array and sets it to -1
		for(int in0 = 0; in0 < runs; in0++){
			for(int in1 = 0; in1 < 2; in1++){
				for(int in2 = 0; in1 < 2; in1++){
					for(int in3 = 0; in1 < 2; in1++){
						for(int in4 = 0; in1 < 2; in1++){
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
		
		//if there is no solution for that reaction do the following
		if(solution[run][ inputs[0] ][ inputs[1] ][ inputs[2] ][ inputs[3] ] == -1){
			randomizeSolution(run,inputs);//randomizes that solution
		}
		
		return solution[run][inputs[0]][inputs[1]][inputs[2]][inputs[3]];
		
	}
	
	public void randomizeSolution(int run, int[] inputs){
		solution[run][inputs[0]][inputs[1]][inputs[2]][inputs[3]] = random.nextInt(3);//sets a random output between 0 and 3 for that input set
	}
}
