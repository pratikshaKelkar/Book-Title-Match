package match.client;

import java.util.Arrays;
import java.util.HashMap;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import match.dao.BookDao;

import match.model.Book;

/**
 * @author PratikshaK class compares book titles from a client application to
 *         book titles stored in a database and returns true of false if titles
 *         match or not.
 */

@Component
public final class BookMatchClient {
	private static final Logger logger = Logger.getLogger(BookMatchClient.class);
	@Autowired
	private BookDao bookDaoImpl;

	public static void main(String[] args) {
		BookMatchClient bookMatchClient = startFramework();
		bookMatchClient.matchBookTitle();
	}

	static BookMatchClient startFramework() {
		ApplicationContext ac = new ClassPathXmlApplicationContext("/applicationContext.xml");
		return ac.getBean(BookMatchClient.class);
	}

	/**
	 * A value after comparing book titles from a client application to book titles
	 * stored in a database and returns true of false if titles match.
	 * 
	 * @return map value after comparison of two title values
	 */
	public Map<String, Boolean> matchBookTitle() {
		List<Book> bookList = bookDaoImpl.fetchBookList();

		Map<String, Boolean> bookTitleMap = new HashMap<String, Boolean>();

		String inputTitle;

		for (BooksToMatch bookTitle : BooksToMatch.values()) {
			inputTitle = bookTitle.getTitle();
			inputTitle = removeNoiseWords(inputTitle);

			// removed punctuation and whitespace
			String tokenizedInputBookName = removePunctuations(inputTitle);
			if (bookList != null) {
				for (Book book : bookList) {
					String bookName = book.getBookTitle();

					bookName = removeNoiseWords(bookName);
					// removed punctuation and whitespace
					String tokenizedDbBookName = removePunctuations(bookName);

					if (tokenizedInputBookName.equalsIgnoreCase(tokenizedDbBookName)) {
						bookTitleMap.put(inputTitle + ": Exists --> ", true);
						break;
					} else {
						bookTitleMap.put(inputTitle + ": Exists --> ", false);
					}
				}
			} else {
				logger.error("error retrieving values from the database");
			}
		}
		for (Map.Entry<String, Boolean> entry : bookTitleMap.entrySet()) {
			logger.info(entry.getKey() + entry.getValue());
		}
		return bookTitleMap;
	}

	/**
	 * @param bookName
	 * @param matches
	 * @return a string after removal of matching words
	 */
	public String removeNoiseWords(String bookName) {
		List<String> noiseWords = Arrays.asList("the", "in", "of", "and");

		String[] tokens = bookName.split("\\s+");
		for (String token : tokens) {
			if (noiseWords.contains(token.toLowerCase())) {
				bookName = bookName.replaceAll(token, "");
			}
		}
		return bookName;
	}

	/**
	 * @param inputTitle
	 * @return removes special characters and white spaces
	 */
	public String removePunctuations(String inputTitle) {
		return inputTitle.replaceAll("[^a-zA-Z0-9]", "");
	}
}
