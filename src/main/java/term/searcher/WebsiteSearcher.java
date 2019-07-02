package term.searcher;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class WebsiteSearcher {
	
	private static final int MAX_THREADS = 20;
	private static final String DEFAULT_TERM = "cats";
	private static final String FILENAME = "urls.txt";
	
	private String filename;
	private String term;
	private Thread[] threads;
	private Map<Thread, SearcherRunnable> processMap;
	private List<String> urlsToCheck;
	
	public static void main(String[] args) {
		String term = DEFAULT_TERM;
		
		WebsiteSearcher ws = new WebsiteSearcher(FILENAME, term);
		List<String> result = ws.doSearch();
		System.out.println("*** Matches for \"" + term + "\" ***");
		for (String str : result) {
			System.out.println(str);
		}
		System.exit(0);
	}
	
	private void startThread(int urlIndex, int threadIndex) {
		String url = "https://" + this.urlsToCheck.get(urlIndex);
		SearcherRunnable r = new SearcherRunnable(url, this.term);
		Thread t = new Thread(r);
		this.threads[threadIndex] = t;
		this.processMap.put(t, r);
		t.start();	
	}
	
	public List<String> parseFile() {
		List<String> urls = new ArrayList<>();
		InputStream in = this.getClass().getResourceAsStream(this.filename);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		
		String line;
		try {
			line = br.readLine();
			while ((line = br.readLine()) != null) { 
				String url = line.split(",")[1];
				url = url.replace("\"", "");
				urls.add(url);
			}
		} catch (IOException e) {
			return urls;
		}
		return urls;
	}
	
	public WebsiteSearcher(String filename, String term) {
		this.filename = filename;
		this.term = term;
	}
	
	public List<String> doSearch() {
		this.urlsToCheck = parseFile();
		
		int threadCount = MAX_THREADS;
		if (this.urlsToCheck.size() < MAX_THREADS) {
			threadCount = this.urlsToCheck.size();
		}
		this.threads = new Thread[threadCount];
		this.processMap = new HashMap<>();
		
		for (int i = 0; i < threadCount; i++) {
			startThread(i, i);
		}
		
		int nextUrlIndex = threadCount;
		int nextThreadIndex = 0;
		
		List<String> success = new LinkedList<String>();
		
		while (nextUrlIndex < this.urlsToCheck.size()) {
			boolean searchStarted = false;
			while (!searchStarted) {
				Thread t = threads[nextThreadIndex];
				if (!t.isAlive()) {
					SearcherRunnable sr = processMap.get(t);
					if (sr.isStarted() && sr.isFinished()) {
						if (sr.isTermFound()) {
							success.add(sr.getUrlString());
							System.out.println("Found at " + sr.getUrlString());
						}
						processMap.remove(t);
						startThread(nextUrlIndex, nextThreadIndex);	
						searchStarted = true;
					}
				}
				nextThreadIndex = (nextThreadIndex + 1) % threadCount;
			}
			nextUrlIndex++;	
		}
		
		System.out.println("All searches initialized, wrapping up");
		for (int i = 0; i < threadCount; i++) {
			Thread t = threads[i];
			long now = System.currentTimeMillis();
			SearcherRunnable sr = processMap.get(t);
			System.out.println("Waiting on thread " + i);
			long duration = System.currentTimeMillis()- sr.getStartTime();
			while (t.isAlive() && duration < 5000) {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {}
			}
			if (t.isAlive()) {
				System.out.println("Skipping thread " + i + ", lasted more than 5 minutes");
				continue;
			}
			System.out.println("Completed thread " + i);
			while (!sr.isFinished()) {}
			System.out.println("Runnable completed on thread " + i);
			if (sr.isTermFound()) {
				success.add(sr.getUrlString());
				System.out.println("Found at " + sr.getUrlString());
			}	
		}
		
		return success;	
	}

}
