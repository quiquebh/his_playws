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
public class JPASourceRepository implements SourceRepository{

    private final JPAApi jpaApi;
    private final DatabaseExecutionContext executionContext;

    @Inject
    public JPASourceRepository(JPAApi jpaApi, DatabaseExecutionContext executionContext) {
        this.jpaApi = jpaApi;
        this.executionContext = executionContext;
    }

    // Implementation of Repository methods
    @Override
    public CompletionStage<Source> add(Source Source) {
        return supplyAsync(() -> wrap(em -> insert(em, Source)), executionContext);
    }

    @Override
    public CompletionStage<Stream<Source>> list() {
        return supplyAsync(() -> wrap(em -> list(em)), executionContext);
    }

    @Override
    public CompletionStage<Stream<Source>> get(Long id) {
        return supplyAsync(() -> wrap(em -> get(em, id)), executionContext);
    }

    @Override
    public CompletionStage<Stream<Source>> searchByName(String name) {
        return supplyAsync(() -> wrap(em -> searchByName(em, name)), executionContext);
    }

    // JPA methods
    private <T> T wrap(Function<EntityManager, T> function) {
        return jpaApi.withTransaction(function);
    }

    private Source insert(EntityManager em, Source Source) {
        em.persist(Source);
        return Source;
    }

    private Stream<Source> list(EntityManager em) {
        List<Source> Sources = em.createQuery("select s from Source s", Source.class).getResultList();
        return Sources.stream();
    }

    private Stream<Source> get(EntityManager em, Long id) {
        List<Source> sources = em.createQuery("select s from Source s where id=" + id, Source.class).getResultList();
        return sources.stream();
    }

    private Stream<Source> searchByName(EntityManager em, String name) {
        List<Source> sources = em.createQuery("select s from Source s where s.name LIKE '%" + name + "%'", Source.class).getResultList();
        return sources.stream();
    }
}
