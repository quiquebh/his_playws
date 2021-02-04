package models;

import com.google.inject.ImplementedBy;

import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

/**
 * This interface provides a non-blocking API for possibly blocking operations.
 */
@ImplementedBy(JPAAuthorRepository.class)
public interface AuthorRepository {

    CompletionStage<Author> add(Author author);

    CompletionStage<Stream<Author>> list();

    CompletionStage<Stream<Author>> get(Long id);

    CompletionStage<Stream<Author>> searchByName(String name);
}