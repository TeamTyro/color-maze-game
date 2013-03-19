package etc;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public class ErrorReport {
	String error;
	
	public ErrorReport() {
		error = "ERROR: No error given!\n";
	}
	
	public ErrorReport(String s_error) {
		error = s_error;
	}
	
	public void setError(String s_error) {
		error = s_error;
	}
	
	public void makeFile() {
		Writer out = null;
		File eFile = new File("report.txt");
		try {
			out = new BufferedWriter(new FileWriter(eFile));
			out.write(error);
			out.close();
		} catch(IOException ex) {
			System.out.printf("ERROR: Major irony! Error creating error report!\n");
		}
	}
}
