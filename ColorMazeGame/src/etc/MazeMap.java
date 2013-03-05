package etc;

import java.util.*;
import java.io.*;
import etc.Constants;

public class MazeMap {
	private static int map [] [];
	
	public MazeMap() {
		map = new int [Constants.MAP_WIDTH][Constants.MAP_HEIGHT];
	}
	
	public int getSpace(int s_x, int s_y) {
		return map[s_x][s_y];
	}
	
	public boolean loadMap(String s_filename) {
		boolean success = false;
		try {
			BufferedReader in = new BufferedReader(new FileReader(s_filename));
			
			int x = 0;
			int y = 0;
			
			int section = 0;
			while ((section = in.read()) != 0 && y < Constants.MAP_HEIGHT) {
				switch(section) {
				case 'b':
					map[x][y] = Constants.MAP_BLOCK;
					break;
				case 'c':
					map[x][y] = Constants.MAP_SPACE;
					break;
				case 's':
					map[x][y] = Constants.MAP_START;
					break;
				case 'w':
					map[x][y] = Constants.MAP_WIN;
					break;
				}
				if(x < Constants.MAP_WIDTH-1) {
					x++;
				} else {
					x=0;
					y++;
				}
			}
			
			success = true;
			in.close();
		} catch(IOException ex) {
			System.out.printf("ERROR: Couldn't load map\n");
			success = false;
		}
		
		return success;
	}
}