package term.searcher;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class SearcherRunnable implements Runnable {
	
	public static final String DEFAULT = "";
	
	private boolean started = false;
	private boolean finished = false;
	private boolean termFound = false;
	private String urlString;
	private String term;
	private long startTime;
	
	public SearcherRunnable(String url, String term) {
		this.urlString = url;
		this.term = term;
		this.startTime = System.currentTimeMillis();
	}
	
	public String getUrlString() {
		return this.urlString;
	}
	
	public boolean isStarted() {
		return this.started;
	}
	
	public boolean isFinished() {
		return this.finished;
	}
	
	public boolean isTermFound() {
		return this.termFound;
	}
	
	public long getStartTime() {
		return this.startTime;
	}

	public String getPage()  {
		StringBuffer response = new StringBuffer();
		
		URL url = null;
		
		try {
			url = new URL(this.urlString);
		} catch (MalformedURLException e1) {
			System.out.println("Invalid url: " + this.urlString);
			return DEFAULT;
		}
		
		HttpURLConnection con = null;
		try {
			con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
				BufferedReader in = new BufferedReader(
						new InputStreamReader(con.getInputStream()));
				String readLine = in.readLine();
				while (readLine != null) {
					response.append(readLine);
					readLine = in.readLine();
				}
				in.close();
			}
		} catch (IOException e) {
			System.out.println("Unable to retrieve: " + this.urlString);
			return DEFAULT;
		} finally {
			if (con != null) {
				con.disconnect();
			}
		}
		return response.toString();
	}
	
	public boolean termInPage(String contents) {
		
		if (contents == null || contents.isEmpty()) {
			return false;
		}
		
		StringBuilder regexBuilder = new StringBuilder();
		regexBuilder.append(".*");
		for (char c : this.term.toCharArray()) {
			regexBuilder.append('(');
			regexBuilder.append(Character.toLowerCase(c));
			regexBuilder.append('|');
			regexBuilder.append(Character.toUpperCase(c));
			regexBuilder.append(')');
		}
		regexBuilder.append(".*");
		String regex = regexBuilder.toString();
		
		if (contents.matches(regex)) {
			return true;
		}
		
		return false;
	}
	
	@Override
	public void run() {
		this.started = true;
		String contents = getPage();
		this.termFound = termInPage(contents);
		this.finished = true;
	}
}
