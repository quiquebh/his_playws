package models;

import com.google.inject.ImplementedBy;

import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

/**
 * This interface provides a non-blocking API for possibly blocking operations.
 */
@ImplementedBy(JPAPublicationRepository.class)
public interface PublicationRepository {

    CompletionStage<Publication> add(Publication publication);

    CompletionStage<Stream<Publication>> list();

    CompletionStage<Stream<Publication>> get(Long id);

    CompletionStage<Stream<Publication>> searchByTitle(String title);

    CompletionStage<Publication> fullAdd(Publication publication);

    CompletionStage<Publication> addAuthor(Long id, Long aid);

    CompletionStage<Publication> addSource(Long id, Long sid);

    CompletionStage<Publication> addKeyword(Long id, Long kid);

}