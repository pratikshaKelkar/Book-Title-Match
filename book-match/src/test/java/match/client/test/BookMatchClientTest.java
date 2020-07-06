package match.client.test;

import static org.junit.Assert.assertEquals;

import static org.mockito.Mockito.when;

import java.util.ArrayList;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import org.mockito.MockitoAnnotations;

import org.mockito.InjectMocks;

import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import match.client.BookMatchClient;

import match.dao.BookDao;

import match.model.Book;

@RunWith(PowerMockRunner.class)
@PrepareForTest(BookMatchClient.class)
@PowerMockIgnore("javax.management.*")
public class BookMatchClientTest {
	private static final Logger logger = Logger.getLogger(BookMatchClientTest.class);
	@Mock
	BookDao bookDaoImpl;

	@InjectMocks
	BookMatchClient bookMatchClient = new BookMatchClient();;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testMatchTitleReturnSize() {
		logger.info("Test - testMatchTitleReturnSize - Start");
		List<Book> bookList = new ArrayList<Book>();
		Book book1 = new Book();
		book1.setBookTitle("Shoulder Arthroplasty");

		bookList.add(book1);

		when(bookDaoImpl.fetchBookList()).thenReturn(bookList);

		Map<String, Boolean> bookTitleMap = bookMatchClient.matchBookTitle();

		assertEquals(true, bookTitleMap.get("Shoulder Arthroplasty" + ": Exists --> "));

		logger.info("Test - testMatchTitleReturnSize - End");
	}

	@Test
	public void testMatchTitleCheckingBooleanValue() {
		logger.info("Test - testMatchTitleChecking - Start");
		List<Book> bookList = new ArrayList<Book>();
		Book book1 = new Book();
		book1.setBookTitle("Shoulder Arthroplasty");
		Book book2 = new Book();
		book2.setBookTitle("Head AND Neck Imaging");
		Book book3 = new Book();
		book3.setBookTitle("Shackelford''s Surgery of the Alimentary Tract");
		Book book4 = new Book();
		book4.setBookTitle("Aids Therapy");
		Book book5 = new Book();
		book5.setBookTitle("Andrews Diseases Skin Clinical Dermatology");

		bookList.add(book1);
		bookList.add(book2);
		bookList.add(book3);
		bookList.add(book4);
		bookList.add(book5);

		when(bookDaoImpl.fetchBookList()).thenReturn(bookList);

		Map<String, Boolean> bookTitleMap = bookMatchClient.matchBookTitle();
		String title;
		for (Map.Entry<String, Boolean> entry : bookTitleMap.entrySet()) {
			title = entry.getKey();
			if (title.equals("Good Night Moon")) {
				assertEquals(false, entry.getValue());
			}
		}
		Assert.assertNotNull("Provided Map is null;", bookTitleMap);
		logger.info("Test - testMatchTitleChecking - End");
	}

	@Test
	public void testMatchTitleHavingDigitsInTitle() {
		logger.info("Test - testMatchTitleHavingDigitsInTitle - Start");
		List<Book> bookList = new ArrayList<Book>();
		Book book1 = new Book();
		book1.setBookTitle("Shoulder123 Arthroplasty");

		bookList.add(book1);

		when(bookDaoImpl.fetchBookList()).thenReturn(bookList);

		Map<String, Boolean> bookTitleMap = bookMatchClient.matchBookTitle();

		assertEquals(false, bookTitleMap.get("Shoulder Arthroplasty" + ": Exists --> "));

		logger.info("Test - testMatchTitleHavingDigitsInTitle - End");
	}

	@Test
	public void testMatchTitleForNullListFromDB() {
		logger.info("Test - testMatchTitleForNullTestListFromDB - Start");
		List<Book> bookList = null;

		when(bookDaoImpl.fetchBookList()).thenReturn(bookList);

		Map<String, Boolean> bookTitleMap = bookMatchClient.matchBookTitle();
		assertEquals(0, bookTitleMap.size());

		logger.info("Test - testMatchTitleForNullTestListFromDB - End");
	}
}