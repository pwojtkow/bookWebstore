package pl.spring.demo.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import pl.spring.demo.enumerations.BookStatus;
import pl.spring.demo.service.BookService;
import pl.spring.demo.to.BookTo;

/**
 * Book rest service
 * 
 * @author PWOJTKOW
 */
@Controller
@ResponseBody
public class BookRestService {

	@Autowired
	BookService bookService;

	/**
	 * Method add book with "FREE" book status
	 * 
	 * @param title
	 *            - title of book to add
	 * @param authors
	 *            - authors of book to add
	 * @return - book transfer object that has been added and http status "OK"
	 */
	@RequestMapping(value = "/rest/books/addFree", method = RequestMethod.PUT)
	public ResponseEntity<BookTo> addFreeBook(@RequestParam("title") String title,
			@RequestParam("authors") String authors) {
		BookTo currentBook = new BookTo(title, authors, BookStatus.FREE);
		bookService.saveBook(currentBook);
		return new ResponseEntity<BookTo>(currentBook, HttpStatus.OK);
	}

	/**
	 * Method returns all books in database
	 * 
	 * @return list of all book transfer objects from database and http status
	 *         "OK" when everything went good, or http status "NO_CONTENT" when
	 *         no books in database
	 */
	@RequestMapping(value = "/rest/books", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<BookTo>> getAllBooks() {
		List<BookTo> allBooks = bookService.findAllBooks();
		if (allBooks.isEmpty()) {
			return new ResponseEntity<List<BookTo>>(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<List<BookTo>>(allBooks, HttpStatus.OK);
	}

	/**
	 * Method gets book with specific id
	 * 
	 * @param id
	 *            - id mapped to book
	 * @return book transfer object with specific id and http status "OK" when
	 *         everything went good, or http status "NOT_FOUND" when no book
	 *         with this id in database
	 */
	@RequestMapping(value = "/rest/books/book", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<BookTo> getBookById(@RequestParam("id") Long id) {
		BookTo book = bookService.findBookById(id);
		if (book == null) {
			return new ResponseEntity<BookTo>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<BookTo>(book, HttpStatus.OK);
	}

	/**
	 * Method add book to database
	 * 
	 * @param title
	 *            - title of book to add
	 * @param authors
	 *            - authors of book to add
	 * @param status
	 *            - status of book to add ("FREE","LOAN","MISSING")
	 * @return -
	 */
	@RequestMapping(value = "/rest/books/add", method = RequestMethod.PUT)
	public ResponseEntity<BookTo> addBook(@RequestParam("title") String title, @RequestParam("authors") String authors,
			@RequestParam("status") String status) {

		BookStatus bookStatus = BookStatus.valueOf(status.toUpperCase());
		BookTo bookToAdd = new BookTo(title, authors, bookStatus);
		bookService.saveBook(bookToAdd);

		return new ResponseEntity<BookTo>(bookToAdd, HttpStatus.CREATED);
	}

	/**
	 * Method delete book with specific id
	 * 
	 * @param id
	 *            - id mapped to book which has to be deleted
	 * @return - book transfer object with data from deleted book and http
	 *         status "OK" when everything went good, or http status "NOT_FOUND"
	 *         when database has not book with this id
	 */
	@RequestMapping(value = "/rest/books/delete", method = RequestMethod.DELETE)
	public ResponseEntity<BookTo> deleteBook(@RequestParam("id") Long id) {
		BookTo book = bookService.findBookById(id);
		if (book == null) {
			return new ResponseEntity<BookTo>(HttpStatus.NOT_FOUND);
		}
		bookService.deleteBook(id);
		return new ResponseEntity<BookTo>(book, HttpStatus.OK);
	}

	/**
	 * Method delete all books from database
	 * 
	 * @return - text message confirm deleting all books and http status "OK"
	 *         when everything went good
	 */
	@RequestMapping(value = "/rest/books/deleteAll", method = RequestMethod.DELETE)
	public ResponseEntity<String> deleteAllBooks() {
		List<BookTo> allBooks = bookService.findAllBooks();
		for (BookTo book : allBooks) {
			bookService.deleteBook(book.getId());
		}
		return new ResponseEntity<String>("All books deleted", HttpStatus.OK);
	}

	/**
	 * Method edits parameters of book in database
	 * 
	 * @param id
	 *            - book id to change
	 * @param title
	 *            - new title of book
	 * @param authors
	 *            - new authors of book
	 * @param status
	 *            - new status of book ("FREE", "LOAN", "MISSING")
	 * @return - book transfer object with changed parameters and http status
	 *         "OK" when everything went good, or http status "NOT_FOUND" when
	 *         book not found in database
	 */
	@RequestMapping(value = "/rest/books/edit", method = RequestMethod.PUT)
	public ResponseEntity<BookTo> editBook(@RequestParam("id") Long id, @RequestParam("title") String title,
			@RequestParam("authors") String authors, @RequestParam("status") String status) {
		BookTo book = bookService.findBookById(id);
		if (book == null) {
			return new ResponseEntity<BookTo>(HttpStatus.NOT_FOUND);
		}
		book.setTitle(title);
		book.setAuthors(authors);
		book.setStatus(BookStatus.valueOf(BookStatus.class, status.toUpperCase()));
		bookService.saveBook(book);
		return new ResponseEntity<BookTo>(book, HttpStatus.OK);
	}

}
