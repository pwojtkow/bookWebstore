package pl.spring.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import pl.spring.demo.constants.ModelConstants;
import pl.spring.demo.constants.ViewNames;
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
	 * Method collects info about all books
	 */
	@RequestMapping(value = "/all", method = RequestMethod.GET) // typ metody która ma zostac wywołana po wywołaniu metody
	public ModelAndView allBooks() {
		ModelAndView modelAndView = new ModelAndView();
		List<BookTo> allBooksList = bookService.findAllBooks();
		modelAndView.addObject(ModelConstants.BOOK_LIST, allBooksList); // oddelegowanie logiki biznesowej do serwisu
		modelAndView.setViewName(ViewNames.BOOKS);
		return modelAndView;
	}

	@RequestMapping(value = "/book", method = RequestMethod.GET)
	public ModelAndView bookDetails(@RequestParam("id") Long id) {
		ModelAndView mav = new ModelAndView();
		mav.addObject(ModelConstants.BOOK, bookService.findBookById(id));
		mav.setViewName(ViewNames.BOOK);
		return mav;
	}
	
	@RequestMapping(value = "/delete")
	public ModelAndView bookDelete(@RequestParam("id") Long id) {
		ModelAndView mav = new ModelAndView();
		mav.addObject(ModelConstants.BOOK, bookService.findBookById(id));
		bookService.deleteBookById(id);
		mav.setViewName(ViewNames.BOOK_DELETED);
		return mav;
	}
	// TODO: Implement GET / POST methods for "add book" functionality

	/**
	 * Binder initialization
	 */
	@InitBinder
	public void initialiseBinder(WebDataBinder binder) {
		binder.setAllowedFields("id", "title", "authors", "status");
	}

}
