package pl.spring.demo.web.rest;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import pl.spring.demo.enumerations.BookStatus;
import pl.spring.demo.service.BookService;
import pl.spring.demo.to.BookTo;
import pl.spring.demo.web.utils.FileUtils;

/**
 * Book rest service tests
 * 
 * @author PWOJTKOW
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@WebAppConfiguration
public class BookRestServiceTest {

	@Autowired
	private BookService bookService;
	@Autowired
	private WebApplicationContext wac;

	private MockMvc mockMvc;

	@Before
	public void setUp() {
		Mockito.reset(bookService);
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
	}

	/**
	 * Test should get all books
	 * 
	 * @throws Exception
	 *             when problem with Mockito
	 */
	@Test
	public void testShouldGetAllBooks() throws Exception {

		// given:
		final BookTo bookTo1 = new BookTo(1L, "title", "Author1", BookStatus.FREE);

		Mockito.when(bookService.findAllBooks()).thenReturn(Arrays.asList(bookTo1));
		// when
		ResultActions response = this.mockMvc
				.perform(get("/rest/books").accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON));
		// then
		response.andExpect(status().isOk())//
				.andExpect(jsonPath("[0].id").value(bookTo1.getId().intValue()))
				.andExpect(jsonPath("[0].title").value(bookTo1.getTitle()))
				.andExpect(jsonPath("[0].authors").value(bookTo1.getAuthors()));
	}

	/**
	 * Test should save book into database
	 * 
	 * @throws Exception
	 *             when problem with Mockito
	 */
	@Test
	public void testShouldSaveBook() throws Exception {
		// given
		File file = FileUtils.getFileFromClasspath("classpath:pl/spring/demo/web/json/bookToSave.json");
		String json = FileUtils.readFileToString(file);
		// when
		ResultActions response = this.mockMvc.perform(put("/rest/books/add").accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON).content(json.getBytes()));
		// then
		response.andExpect(status().isCreated());
	}

	/**
	 * Test should get book by Id
	 * 
	 * @throws Exception
	 *             when problem with Mockito
	 */
	@Test
	public void testSouldGetBookById() throws Exception {
		// given:
		final BookTo bookTo = new BookTo(1L, "Test title", "Test authors", BookStatus.FREE);
		Mockito.when(bookService.findBookById(Mockito.anyLong())).thenReturn(bookTo);
		// when
		ResultActions response = this.mockMvc.perform(get("/rest/books/book").param("id", "1")
				.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON));

		response.andExpect(status().isOk())//
				.andExpect(jsonPath("id").value(bookTo.getId().intValue()))
				.andExpect(jsonPath("title").value(bookTo.getTitle()))
				.andExpect(jsonPath("authors").value(bookTo.getAuthors()))
				.andExpect(jsonPath("status").value(bookTo.getStatus().toString()));
	}

	/**
	 * Test should delete book by Id
	 * 
	 * @throws Exception
	 *             when problem with Mockito
	 */
	@Test
	public void testShouldDeleteBookById() throws Exception {
		// given
		final BookTo bookTo = new BookTo(1L, "Test title", "Test authors", BookStatus.FREE);
		final Long bookId = 2L;
		// when
		Mockito.when(bookService.findBookById(Mockito.anyLong())).thenReturn(bookTo);
		ResultActions response = this.mockMvc
				.perform(delete("/rest/books/delete").param("id", bookId.toString().substring(0, 1)));
		// then
		response.andExpect(status().isOk())//
				.andExpect(jsonPath("id").value(bookTo.getId().intValue()))
				.andExpect(jsonPath("title").value(bookTo.getTitle()))
				.andExpect(jsonPath("authors").value(bookTo.getAuthors()))
				.andExpect(jsonPath("status").value(bookTo.getStatus().toString()));
	}

	/**
	 * Test should delete all books from database
	 * 
	 * @throws Exception
	 *             when problem with Mockito
	 */
	@Test
	public void testShouldDeleteAllBooks() throws Exception {
		// given
		String expectedMessage = "\"All books deleted\"";
		// when
		Mockito.when(bookService.findAllBooks()).thenReturn(Arrays.asList(new BookTo(), new BookTo()));
		ResultActions response = this.mockMvc.perform(delete("/rest/books/deleteAll"));
		// then
		verify(bookService, times(2)).deleteBook(Mockito.anyLong());
		response.andExpect(status().isOk()).andExpect(content().string(expectedMessage));
	}

	/**
	 * Test should edit book
	 * 
	 * @throws Exception
	 *             when problem with Mockito
	 */
	@Test
	public void testShouldEditBook() throws Exception {
		// given
		File file = FileUtils.getFileFromClasspath("classpath:pl/spring/demo/web/json/bookToSave.json");
		String json = FileUtils.readFileToString(file);
		// when
		ResultActions response = this.mockMvc.perform(put("/rest/books/edit").param("book", json)
				.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON).content(json.getBytes()));

		// then
		response.andExpect(status().isOk());
	}

}
