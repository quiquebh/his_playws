package models;

import com.google.inject.ImplementedBy;

import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

/**
 * This interface provides a non-blocking API for possibly blocking operations.
 */
@ImplementedBy(JPAReviewRepository.class)
public interface ReviewRepository {

    CompletionStage<Review> add(Review review);

    CompletionStage<Stream<Review>> list();

    CompletionStage<Stream<Review>> get(Long id);

    CompletionStage<Stream<Review>> searchByPublication(String publication);
}