package pl.spring.demo.web.controller;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static pl.spring.demo.constants.MessagesConstants.BOOK_DELETED_TITLE;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import com.fasterxml.jackson.databind.ObjectMapper;

import pl.spring.demo.controller.BookController;
import pl.spring.demo.enumerations.BookStatus;
import pl.spring.demo.service.BookService;
import pl.spring.demo.to.BookTo;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "controller-test-configuration.xml")
@WebAppConfiguration
public class ValidBookControllerTest {

	@Autowired
	private BookService bookService;

	private MockMvc mockMvc;

	@Before
	public void setup() {
		Mockito.reset(bookService);

		InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
		viewResolver.setPrefix("/WEB-INF/views/");
		viewResolver.setSuffix(".jsp");

		BookController bookController = new BookController();
		mockMvc = MockMvcBuilders.standaloneSetup(bookController).setViewResolvers(viewResolver).build();
		// Due to fact, that We are trying to construct real Bean - Book
		// Controller, we have to use reflection to mock existing field book
		// service
		ReflectionTestUtils.setField(bookController, "bookService", bookService);
	}

	/**
	 * Test should gives add book page
	 * @throws Exception when unable to gives view
	 */
	@Test
	public void testAddBookPage() throws Exception {
		// given
		BookTo testBook = new BookTo(1L, "Test title", "Test Author", BookStatus.FREE);
		Mockito.when(bookService.saveBook(Mockito.any())).thenReturn(testBook);
		ResultActions resultActions = mockMvc.perform(post("/books/add").flashAttr("newBook", testBook));
		// then
		resultActions.andExpect(view().name("addedOrDeleted"))
				.andExpect(model().attribute("newBook", new ArgumentMatcher<Object>() {
					@Override
					public boolean matches(Object argument) {
						BookTo book = (BookTo) argument;
						return null != book && testBook.getTitle().equals(book.getTitle());
					}
				}));
	}

	/**
	 * Test should gives book details page
	 * @throws Exception when unable to gives view
	 */
	@Test
	public void testBookDetailsPage() throws Exception {
		// given
		BookTo testBook = new BookTo(10L, "Test title", "Test Author", BookStatus.FREE);
		Mockito.when(bookService.findBookById(Mockito.any())).thenReturn(testBook);
		// when
		ResultActions resultActions = mockMvc.perform(get("/books/book").param("id", "2"));
		// then
		resultActions.andExpect(view().name("book")).andExpect(model().attribute("book", testBook))
				.andExpect(model().attribute("book", new ArgumentMatcher<Object>() {
					@Override
					public boolean matches(Object argument) {
						BookTo book = (BookTo) argument;
						return null != book && testBook.getTitle().equals(book.getTitle());
					}
				}));
	}

	/**
	 * Test should return error page when empty field in add book action
	 * @throws Exception when unable to gives view
	 */
	@Test
	public void testAddBookWithEmptyFields() throws Exception {
		// given
		BookTo testBook = new BookTo(10L, "Test title", "", BookStatus.FREE);
		String errorMessage = "All fields must be fill";
		// when
		ResultActions resultActions = mockMvc.perform(post("/books/add").flashAttr("newBook", testBook));
		// then
		resultActions.andExpect(view().name("403")).andExpect(model().attribute("errorMessage", errorMessage));
	}

	/**
	 * Test should search book using all fields
	 * @throws Exception when unable to gives view
	 */
	@Test
	public void testSearchUsingAllFields() throws Exception {
		// given
		BookTo testBook = new BookTo(20L, "Second", "Author2", BookStatus.LOAN);
		BookTo searchingBook = new BookTo(null, "Second", "Author2", BookStatus.LOAN);
		List<BookTo> foundBookList = new ArrayList<BookTo>();
		foundBookList.add(testBook);
		// when
		when(bookService.findBooksByAllFields(any(), any(), any())).thenReturn(foundBookList);
		ResultActions resultActions = mockMvc.perform(post("/books/search").flashAttr("book", searchingBook));
		// then
		resultActions.andExpect(view().name("books"))
				.andExpect(model().attribute("bookList", new ArgumentMatcher<Object>() {
					@Override
					public boolean matches(Object argument) {
						List<BookTo> list = (List<BookTo>) argument;

						return list.get(0).getTitle().contains(searchingBook.getTitle());
					}
				}));
	}

	/**
	 * Test should find book using only one field
	 * @throws Exception when unable to gives view
	 */
	@Test
	public void testSearchUsingOneField() throws Exception {
		// given
		BookTo testBook = new BookTo(20L, "Second", "Author2", BookStatus.LOAN);
		BookTo searchingBook = new BookTo(null, "", "Author2", null);
		List<BookTo> foundBookList = new ArrayList<BookTo>();
		foundBookList.add(testBook);
		// when
		when(bookService.findBooksByAllFields(any(), any(), any())).thenReturn(foundBookList);
		ResultActions resultActions = mockMvc.perform(post("/books/search").flashAttr("book", searchingBook));
		// then
		resultActions.andExpect(view().name("books"))
				.andExpect(model().attribute("bookList", new ArgumentMatcher<Object>() {
					@Override
					public boolean matches(Object argument) {
						List<BookTo> list = (List<BookTo>) argument;

						return list.get(0).getTitle().contains(searchingBook.getTitle());
					}
				}));
	}

	/**
	 * Test should find book when in field was given not full name of title
	 * @throws Exception when unable to gives view
	 */
	@Test
	public void testSearchUsingNotFullName() throws Exception {
		// given
		BookTo testBook = new BookTo(20L, "Second", "Author2", BookStatus.LOAN);
		BookTo searchingBook = new BookTo(null, "Sec", "", null);
		List<BookTo> foundBookList = new ArrayList<BookTo>();
		foundBookList.add(testBook);
		// when
		when(bookService.findBooksByAllFields(any(), any(), any())).thenReturn(foundBookList);
		ResultActions resultActions = mockMvc.perform(post("/books/search").flashAttr("book", searchingBook));
		// then
		resultActions.andExpect(view().name("books"))
				.andExpect(model().attribute("bookList", new ArgumentMatcher<Object>() {
					@Override
					public boolean matches(Object argument) {
						List<BookTo> list = (List<BookTo>) argument;

						return list.get(0).getTitle().contains(searchingBook.getTitle());
					}
				}));
	}

	@Test
	public void testDeleteBookPage() throws Exception {
		// given
		BookTo bookToDelete = new BookTo(10L, "Test title", "Test authors", BookStatus.FREE);
		// when
		ResultActions resultActions = mockMvc
				.perform(get("/books/delete").param("id", bookToDelete.getId().toString()));
		// then
		resultActions.andExpect(view().name("addedOrDeleted"))
				.andExpect(model().attribute("addDeleteTitle", BOOK_DELETED_TITLE));
	}

	@Test
	public void testDeleteBookById() throws Exception {
		// given
		BookTo bookToDelete = new BookTo(10L, "Test title", "Test authors", BookStatus.FREE);
		// when
		ResultActions resultActions = mockMvc
				.perform(get("/books/delete").param("id", bookToDelete.getId().toString()));
		// then
		verify(bookService, times(1)).deleteBook(Mockito.anyLong());
	}

	/**
	 * Test should gives all books page
	 * @throws Exception when unable to gives view
	 */
	@Test
	public void testAllBooksPage() throws Exception {
		// given
		List<BookTo> bookList = new ArrayList<BookTo>();
		BookTo book1 = new BookTo();
		BookTo book2 = new BookTo();
		bookList.add(book1);
		bookList.add(book2);
		// when
		when(bookService.findAllBooks()).thenReturn(bookList);
		ResultActions resultActions = mockMvc.perform(get("/books/all"));
		// then
		resultActions.andExpect(view().name("books"))
				.andExpect(model().attribute("bookList", new ArgumentMatcher<Object>() {
					@Override
					public boolean matches(Object argument) {
						List<BookTo> list = (List<BookTo>) argument;
						return list.size() == bookList.size();
					}
				}));
	}

	/**
	 * Test should gives view add book page when invoke method type GET
	 * @throws Exception when unable to gives view
	 */
	@Test
	public void testAddBookPageMethodGet() throws Exception {
		// given
		// when
		ResultActions resultActions = mockMvc.perform(get("/books/add"));
		// then
		resultActions.andExpect(view().name("addBook"));
	}

	/**
	 * Test should give view "search" name
	 * @throws Exception when unable to gives view
	 */
	@Test
	public void testSearchPage() throws Exception {
		// given
		// when
		ResultActions resultActions = mockMvc.perform(get("/books/search"));
		// then
		resultActions.andExpect(view().name("search"));
	}

	/**
	 * (Example) Sample method which convert's any object from Java to String
	 */
	private static String asJsonString(final Object obj) {
		try {
			final ObjectMapper mapper = new ObjectMapper();
			final String jsonContent = mapper.writeValueAsString(obj);
			return jsonContent;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
