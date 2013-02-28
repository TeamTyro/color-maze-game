package sql;

import sql.InfoPackage;
import etc.Constants;

public class XMLParser {
	private static String xml;
	
	public XMLParser() {
		
	}
	
	public XMLParser(InfoPackage pack) {
		String x_0_s = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
		String x_1_s = "<test>";
		String x_1_e = "</test>";
		String x_2_s = "<stime>";
		String x_2_e = "</stime>";
		String x_3_s = "<etime>";
		String x_3_e = "</etime>";
		String x_4_s = "<moves>";
		String x_4_e = "</moves>";
		
		xml = "";
		xml = xml.concat(x_1_s);
		
			xml = xml.concat(x_2_s);
				xml = xml.concat(String.valueOf(pack.getDate(1)));
			xml = xml.concat(x_2_e);
			
			xml = xml.concat(x_3_s);
				xml = xml.concat(String.valueOf(pack.getDate(2)));
			xml = xml.concat(x_3_e);
			
			xml = xml.concat(x_4_s);
				int [] actions = pack.getActions();
				int index = 0;
				while(actions[index] != 0 && index < 500) {
					switch(actions[index]) {
					case Constants.DIR_LEFT:
						xml = xml.concat("l");
						break;
					case Constants.DIR_RIGHT:
						xml = xml.concat("r");
						break;
					case Constants.DIR_UP:
						xml = xml.concat("u");
						break;
					case Constants.DIR_DOWN:
						xml = xml.concat("d");
						break;
					}
					index++;
				}
				
			xml = xml.concat(x_4_e);
		xml = xml.concat(x_1_e);
	}
	
	public String getXML() {
		return xml;
	}
}
