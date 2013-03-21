package sql;

import etc.Constants;

public class InfoPackage {
	private static java.util.Date d_start, d_end;
	private static int [] _actions;
	private static String AgeRange, Ethnicity, Profession, Email;
	
	public InfoPackage () {
		_actions = new int [500];
		for(int i=0; i<500; i++) {
			_actions[i] = 0;
		}
	}
	
	public void setDates(java.util.Date s, java.util.Date e) {
		d_start = s;
		d_end = e;
	}
	
	public void setActions(int [] a) {//sends in action array. a[] is the actions[]
		for(int i=0; i<500; i++) {
			if(a[i] != Constants.DIR_LEFT && a[i] != Constants.DIR_RIGHT && a[i] != Constants.DIR_UP && a[i] != Constants.DIR_DOWN && a[i] != 0) {
				System.out.printf("ERROR (IP): Unexpected action value!\n");
			} else {
				_actions[i] = a[i];
			}
		}
	}
	
	public void setSurvey(String s_AgeRange, String s_Ethnicity, String s_Profession, String s_Email) {
		if(s_AgeRange != null) {
			AgeRange = s_AgeRange;
		} else {
			AgeRange = "ERROR: No good parameter";
		}
		if(s_Ethnicity != null) {
			Ethnicity = s_Ethnicity;
		} else {
			Ethnicity = "ERROR: No good parameter";
		}
		if(s_Profession != null) {
			Profession = s_Profession;
		} else {
			Profession = "ERROR: No good parameter";
		}
		if(s_Email != null) {
			Email = s_Email;
		} else {
			Email = "---";
		}
	}
	
	public int [] getActions() {
		return _actions;
	}
	
	public java.util.Date getDate(int which) {
		if(which == 1) {
			return d_start;
		} else {
			return d_end;
		}
	}
	
	public String getSurvey(int which) {
		switch(which) {
		case 0:
			return AgeRange;
		case 1:
			return Ethnicity;
		case 2:
			return Profession;
		case 3:
			return Email;
		}
		
		return "";
	}
}
