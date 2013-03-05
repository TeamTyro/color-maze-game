package threads;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import sql.DataParser;
import sql.InfoPackage;


public class SendData implements Runnable {
	private DataParser dbInfo;
	
	public SendData(InfoPackage d) {
		dbInfo = new DataParser(d);
	}
	
	public void run() {
		send();
	}
	
	private boolean send() {
		String contentType = "text/xml";
		String charset = "UTF-8";
		String request = null;
		
		request = dbInfo.getData();
		
		URL url = null;
		URLConnection connection = null;
		OutputStreamWriter output = null;
		InputStreamReader response = null;
		
		// Make URL to receiving PHP file
		try {
		    url = new URL("http://jackketcham.com/teamtyro/ext/recieve.php");
		} catch (MalformedURLException e) {
		    e.printStackTrace();
		}

		// Set properties and send data
		try {
		    connection = url.openConnection();
		    connection.setDoOutput(true);
		    connection.setUseCaches(false);
		    connection.setDefaultUseCaches(false);
		    connection.setRequestProperty("Accept-Charset", charset);
		    connection.setRequestProperty("Content-Type", contentType);
		    connection.addRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)");
		    
		    output = new OutputStreamWriter(connection.getOutputStream());
		    output.write(request);
		    if(output != null) {
		    	try {
		    		output.flush();
		    		output.close();
		    	} catch (IOException e) {
		    		System.out.printf("ERROR: Could not close output connection!\n");
		    	}
		    }
		} catch (IOException e) {
		    e.printStackTrace();
		}
		
		// Get server response
		try {
			response = new InputStreamReader(connection.getInputStream());
			StringBuilder buf = new StringBuilder();
		    char[] cbuf = new char[ 2048 ];
		    int num;
		 
		    while ( -1 != (num=response.read( cbuf )))
		    {
		        buf.append( cbuf, 0, num );
		    }
		 
		    String result = buf.toString();
		    System.err.println( "\nResponse from server after POST:\n" + result );
		} catch(IOException e) {
			e.printStackTrace();
		}
		
		return true;
	}
}
