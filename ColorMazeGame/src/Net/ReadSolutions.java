package Net;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import etc.Constants;

public class ReadSolutions {
	
	public static String[] solutions;	//Holds a string of each solution
	public static String file = "mapsolutions.txt";
	
	public ReadSolutions(){
		readSolutions();
	}
	
	public static String[] readSolutions(){
		int solutions = 0;				//How many solutions there are.
		int totalMoves = 0;				//Total amount of moves from each solution added up.
		int maxMoves;				//Which solution had the most moves
	
		try {						//Gets the amount of solutions, so that I can make an array and record them all as a String.
			BufferedReader in = new BufferedReader(new FileReader(file));
			String line = "";
			while ((line = in.readLine()) != null) {	//If there is still a line, then add to int solutions.
				totalMoves += line.length();
				solutions += 1;
			}
			in.close();
			System.out.println("Total Moves: "+totalMoves);
			System.out.println("Total Solutions: "+solutions);
		} catch(IOException ex) {
			System.out.printf("ERROR: Couldn't load map\n");
		}
		String[] textSolutions = new String[solutions];	
		
		
		
		
		try {						//Records the solutions as String onto the solutions[] array.
			
			BufferedReader in = new BufferedReader(new FileReader(file));
			String line = "";
			line = in.readLine();
			while (line != null) {
				System.out.println(line);
				line = in.readLine();
			}
			
			in.close();
		} catch(IOException ex) {
			System.out.printf("ERROR: Couldn't load map\n");
		}
			
		
		
		return textSolutions;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
