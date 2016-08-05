package pl.spring.demo.service;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import pl.spring.demo.entity.BookEntity;
import pl.spring.demo.enumerations.BookStatus;
import pl.spring.demo.to.BookTo;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "CommonServicetest-context.xml")
public class BookServiceTest {

	@Autowired
	private BookService bookService;

	@Test
	public void testFindBooksByAllFieldsUsingAllFields() {
		// given
		List<BookEntity> bookList = new ArrayList<BookEntity>();
		String titleToFind = "first";
		String authorsToFind = "author";
		BookStatus statusToFind = BookStatus.FREE;
		BookEntity book1 = new BookEntity(1L, titleToFind, authorsToFind, statusToFind);
		BookEntity book2 = new BookEntity(2L, "second", "author2", BookStatus.FREE);
		BookEntity book3 = new BookEntity(3L, "third", "author3", BookStatus.FREE);
		bookList.add(book1);
		bookList.add(book2);
		bookList.add(book3);
		List<BookTo> foundBookList = new ArrayList<BookTo>();
		// when
		foundBookList = bookService.findBooksByAllFields(titleToFind, authorsToFind, statusToFind);
		//then
		assertEquals(1, foundBookList.size());
	}

}
