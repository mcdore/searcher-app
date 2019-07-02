package term.searcher;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import term.searcher.SearcherRunnable;

public class SearcherRunnableTest {
	
	@Test
	public void testGetPage() {
		SearcherRunnable subject = new SearcherRunnable("invalid", "cat");
		
		String result = subject.getPage();
		Assert.assertEquals(SearcherRunnable.DEFAULT, result);
		
		subject = new SearcherRunnable("https://www.york.ac.uk/teaching/cws/wws/webpage1.html", "cat");
		result = subject.getPage();
		Assert.assertNotEquals(SearcherRunnable.DEFAULT, result);
	}
	
	@Test
	public void testTermInPage() {
		SearcherRunnable subject = new SearcherRunnable("irrelevant", "cat");
		
		String text = "<HTML><HEAD><TITLE>cats</TITLE><BODY>dogs are great</BODY></HTML>";
		Assert.assertFalse(subject.termInPage(text));
		
		text = "<HTML><HEAD><TITLE>cats</TITLE><BODY>i like cats very much</BODY></HTML>";
		Assert.assertTrue(subject.termInPage(text));
	}
}
