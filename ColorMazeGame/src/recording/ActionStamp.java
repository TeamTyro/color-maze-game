package recording;

public class ActionStamp {
	private static int action;
	private static int secDelay;
	
	public ActionStamp() {
		action = 0;
		secDelay = 0;
	}
	
	public static void setAction(int s_action) {
		action = s_action;
	}
	
	public static int getAction() {
		return action;
	}
	
	public static int getDelay() {
		return secDelay;
	}
}
