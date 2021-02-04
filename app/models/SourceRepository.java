package models;

import com.google.inject.ImplementedBy;

import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

/**
 * This interface provides a non-blocking API for possibly blocking operations.
 */
@ImplementedBy(JPASourceRepository.class)
public interface SourceRepository {

    CompletionStage<Source> add(Source source);

    CompletionStage<Stream<Source>> list();

    CompletionStage<Stream<Source>> get(Long id);

    CompletionStage<Stream<Source>> searchByName(String name);
}