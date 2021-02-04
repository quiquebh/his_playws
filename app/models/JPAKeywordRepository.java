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
public class JPAKeywordRepository implements KeywordRepository{

    private final JPAApi jpaApi;
    private final DatabaseExecutionContext executionContext;

    @Inject
    public JPAKeywordRepository(JPAApi jpaApi, DatabaseExecutionContext executionContext) {
        this.jpaApi = jpaApi;
        this.executionContext = executionContext;
    }

    // Implementation of Repository methods
    @Override
    public CompletionStage<Keyword> add(Keyword Keyword) {
        return supplyAsync(() -> wrap(em -> insert(em, Keyword)), executionContext);
    }

    @Override
    public CompletionStage<Stream<Keyword>> list() {
        return supplyAsync(() -> wrap(em -> list(em)), executionContext);
    }

    @Override
    public CompletionStage<Stream<Keyword>> get(Long id) {
        return supplyAsync(() -> wrap(em -> get(em, id)), executionContext);
    }

    @Override
    public CompletionStage<Stream<Keyword>> searchByName(String name) {
        return supplyAsync(() -> wrap(em -> searchByName(em, name)), executionContext);
    }

    // JPA methods
    private <T> T wrap(Function<EntityManager, T> function) {
        return jpaApi.withTransaction(function);
    }

    private Keyword insert(EntityManager em, Keyword Keyword) {
        em.persist(Keyword);
        return Keyword;
    }

    private Stream<Keyword> list(EntityManager em) {
        List<Keyword> Keywords = em.createQuery("select k from Keyword k", Keyword.class).getResultList();
        return Keywords.stream();
    }

    private Stream<Keyword> get(EntityManager em, Long id) {
        List<Keyword> keywords = em.createQuery("select k from Keyword k where id=" + id, Keyword.class).getResultList();
        return keywords.stream();
    }

    private Stream<Keyword> searchByName(EntityManager em, String name) {
        List<Keyword> keywords = em.createQuery("select k from Keyword k where k.name LIKE '%" + name + "%'", Keyword.class).getResultList();
        return keywords.stream();
    }
}
