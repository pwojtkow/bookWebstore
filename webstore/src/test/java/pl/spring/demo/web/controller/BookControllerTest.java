/**
 * 
 */
package pl.spring.demo.web.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import pl.spring.demo.constants.ModelConstants;
import pl.spring.demo.controller.BookController;
import pl.spring.demo.enumerations.BookStatus;
import pl.spring.demo.service.BookService;
import pl.spring.demo.to.BookTo;

/**
 * Book Controller tests
 * @author PWOJTKOW
 */
@RunWith(MockitoJUnitRunner.class)
public class BookControllerTest {

	private MockMvc mockMvc;
	
	@Mock
	private BookService bookService;

	@InjectMocks
	private BookController bookController;
	
	@Before
	public void setup() {
		InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
		viewResolver.setPrefix("/WEB-INF/templates/");
		viewResolver.setSuffix(".html");

		mockMvc = MockMvcBuilders.standaloneSetup(bookController).setViewResolvers(viewResolver).build();
	}
	
//	@Test
//	public void shouldShowAllBooksInDatabase() throws Exception  {
//		//given 
//		ResultActions resultActions = mockMvc.perform(get("/books/all"));
//		List<BookTo> bookList = new ArrayList<BookTo>();
//		BookTo firstBook = new BookTo(1L,"Pan Tadeusz", "Adam Mickiewicz", BookStatus.FREE);
//		BookTo secondBook = new BookTo(2L, "Nad Niemnem", "Eliza Orzekowa", BookStatus.LOAN);
//		//when
//		bookList.add(firstBook);
//		bookList.add(secondBook);
//		when(bookService.findAllBooks()).thenReturn(bookList);
//		bookController.allBooks();
//		//then
//		resultActions.andExpect(view().name("books"))
//			.andExpect(model().attribute(ModelConstants.BOOK_TITLE, "Pan Tadeusz"));
//	}

}
