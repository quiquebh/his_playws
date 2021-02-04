package models;

import play.db.jpa.JPAApi;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.concurrent.CompletableFuture.supplyAsync;

/**
 * Provide JPA operations running inside of a thread pool sized to the connection pool
 */
public class JPAPublicationRepository implements PublicationRepository{

    private final JPAApi jpaApi;
    private final DatabaseExecutionContext executionContext;

    @Inject
    public JPAPublicationRepository(JPAApi jpaApi, DatabaseExecutionContext executionContext) {
        this.jpaApi = jpaApi;
        this.executionContext = executionContext;
    }

    // Implementation of Repository methods
    @Override
    public CompletionStage<Publication> add(Publication publication) {
        return supplyAsync(() -> wrap(em -> insert(em, publication)), executionContext);
    }

    @Override
    public CompletionStage<Stream<Publication>> list() {
        return supplyAsync(() -> wrap(em -> list(em)), executionContext);
    }

    @Override
    public CompletionStage<Stream<Publication>> get(Long id) {
        return supplyAsync(() -> wrap(em -> get(em, id)), executionContext);
    }

    @Override
    public CompletionStage<Stream<Publication>> searchByTitle(String title) {
        return supplyAsync(() -> wrap(em -> searchByTitle(em, title)), executionContext);
    }

    @Override
    public CompletionStage<Publication> fullAdd(Publication publication) {
        return supplyAsync(() -> wrap(em -> fullInsert(em, publication)), executionContext);
    }

    @Override
    public CompletionStage<Publication> addAuthor(Long id, Long aid) {
        return supplyAsync(() -> wrap(em -> author(em, id, aid)), executionContext);
    }

    @Override
    public CompletionStage<Publication> addSource(Long id, Long sid) {
        return supplyAsync(() -> wrap(em -> source(em, id, sid)), executionContext);
    }

    @Override
    public CompletionStage<Publication> addKeyword(Long id, Long kid) {
        return supplyAsync(() -> wrap(em -> keyword(em, id, kid)), executionContext);
    }

    // JPA methods
    private <T> T wrap(Function<EntityManager, T> function) {
        return jpaApi.withTransaction(function);
    }

    private Publication insert(EntityManager em, Publication publication) {
        em.persist(publication);
        return publication;
    }

    private Publication fullInsert(EntityManager em, Publication publication) {
        if (publication.authors != null) {
            for (Author author : publication.authors) {
                try {
                    Author existingAuthor = em.createQuery("select a from Author a where a.name='" + author.name + "' " +
                            "and a.email='" + author.email + "' " +
                            "and a.affiliation='" + author.affiliation + "'", Author.class).getSingleResult();
                    author.id = existingAuthor.id;
                } catch (javax.persistence.NoResultException e) {
                    em.persist(author);
                }
            }
        }
        if (publication.keywords != null) {
            for (Keyword keyword : publication.keywords) {
                try {
                    Keyword existingKeyword = em.createQuery("select k from Keyword k where k.name='" + keyword.name + "'", Keyword.class).getSingleResult();
                    keyword.id = existingKeyword.id;
                } catch (javax.persistence.NoResultException e) {
                    em.persist(keyword);
                }
            }
        }
        if (publication.source != null) {
            try {
                Source existingSource = em.createQuery("select s from Source s where s.name='" + publication.source.name + "' " +
                            "and s.issn='" + publication.source.issn + "'", Source.class).getSingleResult();
                    publication.source.id = existingSource.id;
                } catch (javax.persistence.NoResultException e) {
                    em.persist(publication.source);
                }
        }
        em.persist(publication);
        return publication;
    }

    private Stream<Publication> list(EntityManager em) {
        List<Publication> publications = em.createQuery("select p from Publication p", Publication.class).getResultList();
        return publications.stream();
    }

    private Stream<Publication> get(EntityManager em, Long id) {
        List<Publication> publications = em.createQuery("select p from Publication p where id=" + id, Publication.class).getResultList();
        return publications.stream();
    }

    private Stream<Publication> searchByTitle(EntityManager em, String title) {
        List<Publication> publications = em.createQuery("select p from Publication p where p.title LIKE '%" + title + "%'", Publication.class).getResultList();
        return publications.stream();
    }

    private Publication author(EntityManager em, Long id, Long aid) {
        Publication pub = this.getPublication(em, id);
        Author auth = this.getAuthor(em, aid);
        pub.authors.add(auth);
        auth.publications.add(pub);
        return pub;
    }

    private Publication source(EntityManager em, Long id, Long sid) {
        Publication pub = this.getPublication(em, id);
        Source sour = this.getSource(em, sid);
        pub.source = sour;
        sour.publications.add(pub);
        return pub;
    }

    private Publication keyword(EntityManager em, Long id, Long kid) {
        Publication pub = this.getPublication(em, id);
        Keyword key = this.getKeyword(em, kid);
        pub.keywords.add(key);
        key.publications.add(pub);
        return pub;
    }

    // Helper methods for Mappings
    private Publication getPublication(EntityManager em, Long id) {
        return em.createQuery("select p from Publication p where id=" + id, Publication.class).getSingleResult();
    }

    private Author getAuthor(EntityManager em, Long aid) {
        return em.createQuery("select a from Author a where id=" + aid, Author.class).getSingleResult();
    }

    private Source getSource(EntityManager em, Long sid) {
        return em.createQuery("select s from Source s where id=" + sid, Source.class).getSingleResult();
    }

    private Keyword getKeyword(EntityManager em, Long kid) {
        return em.createQuery("select k from Keyword k where id=" + kid, Keyword.class).getSingleResult();
    }
}
