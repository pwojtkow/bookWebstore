package pl.spring.demo.controller;

import static pl.spring.demo.constants.MessagesConstants.BOOK_ADDED_HEADER;
import static pl.spring.demo.constants.MessagesConstants.BOOK_ADDED_TITLE;
import static pl.spring.demo.constants.MessagesConstants.BOOK_DELETED_HEADER;
import static pl.spring.demo.constants.MessagesConstants.BOOK_DELETED_TITLE;
import static pl.spring.demo.constants.MessagesConstants.NOT_ALL_FIELDS_FILL;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import pl.spring.demo.constants.ModelConstants;
import pl.spring.demo.constants.ViewNames;
import pl.spring.demo.enumerations.BookStatus;
import pl.spring.demo.service.BookService;
import pl.spring.demo.to.BookTo;

/**
 * Book controller
 * 
 * @author mmotowid
 *
 */
@Controller
@RequestMapping("/books")
public class BookController {

	@Autowired
	private BookService bookService;

	@RequestMapping
	public String list(Model model) {
		return "redirect:/books/all";
	}

	/**
	 * Method return all books from database
	 * @return - all books from database
	 */
	@RequestMapping(value = "/all", method = RequestMethod.GET) 
	public ModelAndView allBooks() {
		ModelAndView modelAndView = new ModelAndView();
		List<BookTo> allBooksList = bookService.findAllBooks();
		modelAndView.addObject(ModelConstants.BOOK_LIST, allBooksList); 
		modelAndView.setViewName(ViewNames.BOOKS);
		return modelAndView;
	}

	/**
	 * Method find book by id and return it's details
	 * @param id
	 *            - id mapped to book to get details
	 * @return - model and view object with book details and view "BOOK" name
	 */
	@RequestMapping(value = "/book", method = RequestMethod.GET)
	public ModelAndView bookDetails(@RequestParam("id") Long id) {
		ModelAndView mav = new ModelAndView();
		mav.addObject(ModelConstants.BOOK, bookService.findBookById(id));
		mav.setViewName(ViewNames.BOOK);
		return mav;
	}

	/**
	 * Method delete book by id
	 * @param id
	 *            - id mapped to book to delete
	 * @return model and view object with deleted book details and view
	 *         "ADDED_OR_DELETED" name
	 */
	@RequestMapping(value = "/delete")
	public ModelAndView bookDelete(@RequestParam("id") Long id) {
		ModelAndView mav = new ModelAndView();
		BookTo foundBook = bookService.findBookById(id);
		mav.addObject(ModelConstants.BOOK, foundBook);
		mav.addObject(ModelConstants.ADD_DELETE_TITLE, BOOK_DELETED_TITLE);
		mav.addObject(ModelConstants.ADD_DELETE_HEADER, BOOK_DELETED_HEADER);
		bookService.deleteBook(id);
		mav.setViewName(ViewNames.ADDED_OR_DELETED);
		return mav;
	}

	/**
	 * Method gives view to add book, and collect info about it
	 * @return model and view object with new book details and view "ADD_BOOK"
	 *         name
	 */
	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public ModelAndView addBookForm() {
		ModelAndView mav = new ModelAndView();
		mav.addObject("newBook", new BookTo());
		mav.setViewName(ViewNames.ADD_BOOK);
		return mav;
	}

	/**
	 * Method get book details from Get Request Method, and add book to database
	 * @param newBook
	 *            - book transfer object passed from GET type method
	 * @return model and view object with new book details and view
	 *         "ADDED_OR_DELETED" name
	 */
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public ModelAndView addBookToDatabase(@ModelAttribute("newBook") BookTo newBook) {
		ModelAndView mav = new ModelAndView();
		String title = newBook.getTitle();
		BookStatus status = newBook.getStatus();
		String authors = newBook.getAuthors();
		if (title.isEmpty() || status == null || authors.isEmpty()) {
			mav.addObject(ModelConstants.ERROR_MESSAGE, NOT_ALL_FIELDS_FILL);
			mav.setViewName(ViewNames._403);
		} else {
			mav.addObject(ModelConstants.ADD_DELETE_HEADER, BOOK_ADDED_HEADER);
			mav.addObject(ModelConstants.ADD_DELETE_TITLE, BOOK_ADDED_TITLE);
			mav.addObject(ModelConstants.BOOK, newBook);
			bookService.saveBook(newBook);
			mav.setViewName(ViewNames.ADDED_OR_DELETED);
		}
		return mav;
	}

	/**
	 * Method gives view to search book by any fields
	 * @return model and view object with book to search details and view
	 *         "SEARCH" name
	 */
	@RequestMapping(value = "/search", method = RequestMethod.GET)
	public ModelAndView searchBook() {
		ModelAndView mav = new ModelAndView();
		mav.addObject("newBook", new BookTo());
		mav.setViewName(ViewNames.SEARCH);
		return mav;
	}

	/**
	 * Method get book details from Get Request Method, and search book in
	 * database
	 * @param newBook
	 *            - book transfer object passed from GET type method
	 * @return model and view object with books details that fits to given
	 *         attribute and view "BOOKS" name
	 */
	@RequestMapping(value = "/search", method = RequestMethod.POST)
	public ModelAndView searchBookInDatabase(@ModelAttribute("newBook") BookTo newBook) {
		ModelAndView mav = new ModelAndView();
		String title = newBook.getTitle();
		String authors = newBook.getAuthors();
		BookStatus status = newBook.getStatus();
		List<BookTo> foundBookList = new ArrayList<BookTo>();
		foundBookList = bookService.findBooksByAllFields(title, authors, status);
		mav.addObject(ModelConstants.BOOK_LIST, foundBookList);
		mav.setViewName(ViewNames.BOOKS);
		return mav;
	}

	/**
	 * Binder initialization
	 */
	@InitBinder
	public void initialiseBinder(WebDataBinder binder) {
		binder.setAllowedFields("id", "title", "authors", "status");
	}

}
