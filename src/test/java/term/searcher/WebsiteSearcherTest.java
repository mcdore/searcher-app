package term.searcher;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class WebsiteSearcherTest {
	
	
//	@Test
	public void testFileReading() {	
		WebsiteSearcher subject = new WebsiteSearcher("urls.txt", "cat");
		List<String> urls = subject.parseFile();
		Assert.assertEquals(urls.size(), 500);		
	}
	
	@Test
	public void testSearching() {
		WebsiteSearcher subject = new WebsiteSearcher("urls.txt", "cat");
		List<String> result = subject.doSearch();
		
		System.out.println(result.size());
		System.out.println(result);
		
	}

}
