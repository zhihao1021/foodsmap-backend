package com.nckueat.foodsmap.repository.postgresql;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import com.nckueat.foodsmap.model.entity.Article;
import jakarta.persistence.EntityManager;


interface ArticleFindByAuthorId {
    default List<Article> findByAuthorId(Long authorId) {
        return findByAuthorId(authorId, 10, null);
    };

    default List<Article> findByAuthorId(Long authorId, Long ack) {
        return findByAuthorId(authorId, 10, ack);
    };

    default List<Article> findByAuthorId(Long authorId, int limit) {
        return findByAuthorId(authorId, limit, null);
    };

    public List<Article> findByAuthorId(Long authorId, int limit, Long ack);
}


class ArticleFindByAuthorIdImpl implements ArticleFindByAuthorId {
    @Autowired
    private EntityManager entityManager;

    @Override
    public List<Article> findByAuthorId(Long authorId, int limit, Long ack) {
        String query = "SELECT a FROM Article a WHERE a.authorId = :authorId";
        if (ack != null) {
            query += " AND a.id < :ack";
        }
        query += " ORDER BY a.id DESC LIMIT :limit";

        var typedQuery = entityManager.createQuery(query, Article.class);
        typedQuery = typedQuery.setParameter("authorId", authorId);
        typedQuery = typedQuery.setParameter("limit", Math.max(Math.min(limit, 100), 1));
        if (ack != null) {
            typedQuery = typedQuery.setParameter("ack", ack);
        }

        return typedQuery.getResultList();
    }
}


public interface ArticleRepository extends JpaRepository<Article, Long>, ArticleFindByAuthorId {
    boolean existsByIdAndAuthorId(Long id, Long authorId);

    Optional<Article> findFirstByIdAndAuthorId(Long id, Long authorId);
}

