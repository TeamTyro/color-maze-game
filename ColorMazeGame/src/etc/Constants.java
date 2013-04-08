package etc;

public class Constants {
	// Directional constants, returned by recActions[]. They are wierd numbers, but the numbers worked best for the ANN
	public static final int DIR_RIGHT = 6;
	public static final int DIR_LEFT = 0;
	public static final int DIR_UP = -4;
	public static final int DIR_DOWN = 2;
	// Map space constants are returned by the map[] array
	public static final int MAP_START = 4;
	public static final int MAP_SPACE = -1;
	public static final int MAP_BLOCK = 1;
	public static final int MAP_WIN = 5;
	// Map property constants
	public static final int MAP_WIDTH = 16;
	public static final int MAP_HEIGHT = 16;
	//For the neural network outputs.
	public static final int positive = 1;
	public static final int negative = 0;
}
