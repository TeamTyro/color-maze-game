package Net;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

public class ReadSolutions {
	
	public static String[] solutions;	//Holds a string of each solution
	public static String file = "mapsolutions.txt";
	public static double outputs[][];
	public static int totalMoves;
	public static int solutionsCount;
	public Random r = new Random(1);
	
	public ReadSolutions(){
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
		double inputs[][] = new double[((int) (percent*totalMoves))][5];				//Makes an array of the appropriate size. (the percent amount of total moves, in int format)
		System.out.println("	Inputs to learn: "+inputs.length);						//This prints the amount of input sets that will be fed into the ANN.
		
		int solutionsRecorded = 0;														//Since the ANN must be fed truly random info, it will just randomly set info until the array is full. This keeps track of how much info has, indeed, been recorded so far.
		while(solutionsRecorded < inputs.length){										//While the array has not been fully filled.
		
			for(int s = 0; s < solutions.length; s++){									//Goes through each move of each solution, picking at random (percent) intervals an input to record.
				for(int l = 0; l < solutions[s].length(); l++){							//Goes through the entire string of that particular solution.
					if(r.nextFloat() < percent && solutionsRecorded < inputs.length){	//If it randomly chooses it, then record that input.
						//System.out.println(""+solutions[s].charAt(l));
						inputs[solutionsRecorded] = getSituation(solutions[s], l);		//Gets the inputs at the time that that situation was recorded.
						solutionsRecorded++;
						
						break;
					}
				}
			}
			
		}
		
		return inputs;
	}
	
	public double[] getSituation(String solution, int move){							//Gets the inputs at the time that a particular move was performed, in the String solution.
		
	}
	
	public double[][] getOutputs(){	//The output array is set inside of the getInputs(percent) method.
		return outputs;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
