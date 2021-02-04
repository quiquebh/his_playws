package models;

import com.google.inject.ImplementedBy;

import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

/**
 * This interface provides a non-blocking API for possibly blocking operations.
 */
@ImplementedBy(JPAKeywordRepository.class)
public interface KeywordRepository {

    CompletionStage<Keyword> add(Keyword keyword);

    CompletionStage<Stream<Keyword>> list();

    CompletionStage<Stream<Keyword>> get(Long id);

    CompletionStage<Stream<Keyword>> searchByName(String name);
}