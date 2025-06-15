package com.nckueat.foodsmap.repository.elasticsearch;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchAggregations;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.lang.NonNull;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nckueat.foodsmap.model.elasticesarch.SearchAfterPage;
import com.nckueat.foodsmap.model.entity.ArticleES;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregate;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;

interface ArticleESTagOperation {
    public SearchAfterPage<Long> findIdsByTag(String tag, int limit, String searchAfterTag);

    public SearchAfterPage<Long> findIdsByTag(String tag, int limit, List<Object> searchAfterValue);

    public List<String> findPopularTags(int limit);
}


class ArticleESTagOperationImpl implements ArticleESTagOperation {
    @Autowired
    private ElasticsearchOperations elasticsearchOperations;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public SearchAfterPage<Long> findIdsByTag(@NonNull String tag, int limit,
            String searchAfterTag) {
        List<Object> searchAfterValue = null;
        if (searchAfterTag != null && !searchAfterTag.isEmpty()) {
            try {
                searchAfterValue = objectMapper.readValue(searchAfterTag,
                        new TypeReference<List<Object>>() {});
            } catch (JsonProcessingException e) {
            }
        }

        return findIdsByTag(tag, limit, searchAfterValue);
    }

    @Override
    public SearchAfterPage<Long> findIdsByTag(@NonNull String tag, int limit,
            List<Object> searchAfterValue) {
        NativeQueryBuilder builder =
                new NativeQueryBuilder().withQuery(new CriteriaQuery(new Criteria("tags").is(tag)))
                        .withFields("id").withSort(Sort.by("id").descending())
                        .withSourceFilter(new FetchSourceFilter(new String[] {"id"}, null))
                        .withPageable(Pageable.ofSize(limit));


        if (searchAfterValue != null && !searchAfterValue.isEmpty()) {
            builder = builder.withSearchAfter(searchAfterValue);
        }

        NativeQuery query = builder.build();
        SearchHits<ArticleES> searchHits = elasticsearchOperations.search(query, ArticleES.class);

        List<Long> ids =
                searchHits.getSearchHits().stream().map(hit -> hit.getContent().getId()).toList();
        List<Object> lastSearchAfter = searchHits.getSearchHits().isEmpty() ? null
                : searchHits.getSearchHits().get(searchHits.getSearchHits().size() - 1)
                        .getSortValues();

        return new SearchAfterPage<Long>(ids, limit, lastSearchAfter);
    }

    @Override
    public List<String> findPopularTags(int limit) {
        NativeQuery baseQuery = new NativeQueryBuilder().withQuery(q -> q.matchAll(m -> m))
                .withSort(Sort.by("id").descending())
                .withPageable(Pageable.ofSize(Math.max(Math.min(limit, 100000), 1))).build();

        Aggregation aggregation = Aggregation.of(a -> a.terms(t -> t.field("tags")));

        NativeQuery aggregationQuery = new NativeQueryBuilder().withQuery(baseQuery.getQuery())
                .withAggregation("popular_tags", aggregation).withMaxResults(0).build();


        SearchHits<ArticleES> searchHits =
                elasticsearchOperations.search(aggregationQuery, ArticleES.class);

        if (searchHits.hasAggregations()) {
            ElasticsearchAggregations aggregationContainer =
                    (ElasticsearchAggregations) searchHits.getAggregations();
            Aggregate aggregate =
                    aggregationContainer.get("popular_tags").aggregation().getAggregate();

            return aggregate.sterms().buckets().array().stream()
                    .map(bucket -> bucket.key().stringValue()).toList();
        }

        return null;
    }
}


public interface ArticleESRepository
        extends ElasticsearchRepository<ArticleES, Long>, ArticleESTagOperation {

}
