package pl.spring.demo.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pl.spring.demo.entity.BookEntity;
import pl.spring.demo.enumerations.BookStatus;
import pl.spring.demo.mapper.BookMapper;
import pl.spring.demo.repository.BookRepository;
import pl.spring.demo.service.BookService;
import pl.spring.demo.to.BookTo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class BookServiceImpl implements BookService {

	@Autowired
	private BookRepository bookRepository;

	@Override
	public List<BookTo> findAllBooks() {
		return BookMapper.map2To(bookRepository.findAll());
	}

	@Override
	public List<BookTo> findBooksByTitle(String title) {
		return BookMapper.map2To(bookRepository.findBookByTitle(title));
	}

	@Override
	public List<BookTo> findBooksByAuthor(String author) {
		return BookMapper.map2To(bookRepository.findBookByAuthor(author));
	}

	@Override
	@Transactional(readOnly = false)
	public BookTo saveBook(BookTo book) {
		BookEntity entity = BookMapper.map(book);
		entity = bookRepository.save(entity);
		return BookMapper.map(entity);
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteBook(Long id) {
		bookRepository.delete(id);

	}

	@Override
	public BookTo findBookById(Long id) {
		return BookMapper.map(bookRepository.findBookById(id));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pl.spring.demo.service.BookService#findBookByTitle(java.lang.String)
	 * It can be done also by Query
	 */
	@Override
	public List<BookTo> findBooksByAllFields(String title, String authors, BookStatus status) {
		List<BookEntity> allBooks = new ArrayList<BookEntity>();
		allBooks.addAll(bookRepository.findAll());
		Set<BookEntity> foundBookSet = new HashSet<BookEntity>();
		
		foundBookSet.addAll(findByTitle(allBooks,title));
		foundBookSet.addAll(findByAuthors(allBooks, foundBookSet, authors));
		foundBookSet.addAll(findByStatus(allBooks,foundBookSet,status));
		
		List<BookEntity> foundBookList = new ArrayList<BookEntity>();
		foundBookList.addAll(foundBookSet);
		return BookMapper.map2To(foundBookList);
	}

	private Set<BookEntity> findByStatus(List<BookEntity> allBooks, Set<BookEntity> foundBookSet, BookStatus status) {
		if (status != null) {
			boolean isFoundBookListEmpty = foundBookSet.isEmpty();
			if (isFoundBookListEmpty) {
				foundBookSet = allBooks.stream().filter(x -> x.getStatus() == status)
						.collect(Collectors.toSet());
			} else {
				List<BookEntity> bufforList = new ArrayList<BookEntity>();
				bufforList.addAll(foundBookSet);
				foundBookSet = bufforList.stream().filter(x -> x.getStatus() == status)
						.collect(Collectors.toSet());
			}
		}
		return foundBookSet;
	}

	private Set<BookEntity> findByAuthors(List<BookEntity> allBooks, Set<BookEntity> foundBookSet, String authors) {
		if (authors != "") {
			boolean isFoundBookListEmpty = foundBookSet.isEmpty();
			if (isFoundBookListEmpty) {
				foundBookSet = allBooks.stream().filter(x -> x.getAuthors().toLowerCase().contains(authors.toLowerCase()))
						.collect(Collectors.toSet());
			} else {
				List<BookEntity> bufforList = new ArrayList<BookEntity>();
				bufforList.addAll(foundBookSet);
				foundBookSet = bufforList.stream().filter(x -> x.getAuthors().equals(authors))
						.collect(Collectors.toSet());
			}
		}
		return foundBookSet;
	}

	private Set<BookEntity> findByTitle(List<BookEntity> allBooks, String title) {
		Set<BookEntity> foundBooks = new HashSet<BookEntity>();
		if (title != "") {
			foundBooks = allBooks.stream().filter(x -> x.getTitle().toLowerCase().contains(title.toLowerCase())).collect(Collectors.toSet());
			System.out.println(foundBooks.size());
		}
		return foundBooks;
	}

}
