package etc;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class MazeMap {
	private static int map [] [];
	
	public MazeMap() {
		map = new int [Constants.MAP_WIDTH][Constants.MAP_HEIGHT];
	}
	
	public int getSpace(int s_x, int s_y) {
		return map[s_x][s_y];
	}
	
	public boolean loadMap(URL s_fileURL) {
		boolean success = false;
		try {			
			s_fileURL.openConnection();
			InputStream reader = s_fileURL.openStream();
			
			int x = 0;
			int y = 0;
			
			int section = 0;
			while ((section = reader.read()) != 0 && y < Constants.MAP_HEIGHT) {
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
		} catch(IOException ex) {
			System.out.printf("ERROR: Couldn't load map\n");
			success = false;
		}
		
		return success;
	}
}