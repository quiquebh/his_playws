package models;

import play.db.jpa.JPAApi;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.concurrent.CompletableFuture.supplyAsync;

/**
 * Provide JPA operations running inside of a thread pool sized to the connection pool
 */
public class JPAReviewRepository implements ReviewRepository{

    private final JPAApi jpaApi;
    private final DatabaseExecutionContext executionContext;

    @Inject
    public JPAReviewRepository(JPAApi jpaApi, DatabaseExecutionContext executionContext) {
        this.jpaApi = jpaApi;
        this.executionContext = executionContext;
    }

    // Implementation of Repository methods
    @Override
    public CompletionStage<Review> add(Review review) {
        return supplyAsync(() -> wrap(em -> insert(em, review)), executionContext);
    }

    @Override
    public CompletionStage<Stream<Review>> list() {
        return supplyAsync(() -> wrap(em -> list(em)), executionContext);
    }

    @Override
    public CompletionStage<Stream<Review>> get(Long id) {
        return supplyAsync(() -> wrap(em -> get(em, id)), executionContext);
    }

    @Override
    public CompletionStage<Stream<Review>> searchByPublication(String publication) {
        return supplyAsync(() -> wrap(em -> searchByPublication(em, publication)), executionContext);
    }

    // JPA methods
    private <T> T wrap(Function<EntityManager, T> function) {
        return jpaApi.withTransaction(function);
    }

    private Review insert(EntityManager em, Review review) {
        em.persist(review);
        return review;
    }

    private Stream<Review> list(EntityManager em) {
        List<Review> reviews = em.createQuery("select a from Review a", Review.class).getResultList();
        return reviews.stream();
    }

    private Stream<Review> get(EntityManager em, Long id) {
        List<Review> reviews = em.createQuery("select a from Review a where id=" + id, Review.class).getResultList();
        return reviews.stream();
    }

    private Stream<Review> searchByPublication(EntityManager em, String publication) {
        List<Review> reviews = em.createQuery("select a from Review a where a.publication LIKE '%" + publication + "%'", Review.class).getResultList();
        return reviews.stream();
    }
}
