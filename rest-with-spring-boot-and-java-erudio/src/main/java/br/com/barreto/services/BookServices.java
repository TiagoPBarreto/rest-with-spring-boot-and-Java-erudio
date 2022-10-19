package br.com.barreto.services;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.barreto.controller.BookController;
import br.com.barreto.controller.PersonController;
import br.com.barreto.data.vo.v1.BookVO;
import br.com.barreto.exception.RequiredObjectIsNullException;
import br.com.barreto.exception.ResourceNotFoundException;
import br.com.barreto.mapper.DozerMapper;
import br.com.barreto.mapper.custom.PersonMapper;
import br.com.barreto.model.Book;
import br.com.barreto.repositories.BookRepository;

@Service
public class BookServices {

	
	private Logger logger = Logger.getLogger(BookServices.class.getName());
	
	@Autowired
	BookRepository repository;
	
	@Autowired
	PersonMapper mapper;
	
	
		public List<BookVO> findAll() {
			logger.info("find all book!");
			var books =   DozerMapper.parseListObjects(repository.findAll(), BookVO.class);
			books.stream().forEach(p -> p.add(linkTo(methodOn(PersonController.class).findById(p.getKey())).withSelfRel()));
			return books;
		}
		
		public BookVO findById(Long id) {
		logger.info("find one book!");
		var entity = repository.findById(id)
				.orElseThrow(()-> new ResourceNotFoundException("No records found for this ID!"));
		var vo = DozerMapper.parseObject(entity, BookVO.class);
		vo.add(linkTo(methodOn(PersonController.class).findById(id)).withSelfRel());
		return vo;
	}
		
		public BookVO create(BookVO book ) {
			
			if(book == null) throw new RequiredObjectIsNullException();
			
			logger.info("Creating one book!");
			var entity = DozerMapper.parseObject(book, Book.class);
			var vo = DozerMapper.parseObject(repository.save(entity), BookVO.class);
			vo.add(linkTo(methodOn(PersonController.class).findById(vo.getKey())).withSelfRel());
			return vo;
			
		}
		
		//public PersonVOV2 createV2(PersonVOV2 book ) {
			//logger.info("Creating one book!");
		//var entity = mapper.convertVoToEntity(book);
			//var vo = mapper.convertEntityToVo( repository.save(entity));
		//	return vo;
			
	//	}
		
		public BookVO update(BookVO book ) {
			
			
			if(book == null) throw new RequiredObjectIsNullException();
			
			logger.info("updating one book!");
			
			var entity =  repository.findById(book.getKey())
			.orElseThrow(()-> new ResourceNotFoundException("No records found for this ID!"));
			
			entity.setAuthor(book.getAuthor());
			entity.setLaunchDate(book.getLaunchDate());
			entity.setPrice(book.getPrice());
			entity.setTitle(book.getTitle());
			
			var vo = DozerMapper.parseObject(repository.save(entity), BookVO.class);
			vo.add(linkTo(methodOn(BookController.class).findById(vo.getKey())).withSelfRel());
			return vo;
		}
		
		public void delete(Long id) {
			logger.info("deleting one book!");
			var entity =  repository.findById(id)
					.orElseThrow(()-> new ResourceNotFoundException("No records found for this ID!"));
			repository.delete(entity);
			
		}
		
}
