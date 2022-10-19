package br.com.barreto.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.barreto.model.Book;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {}
