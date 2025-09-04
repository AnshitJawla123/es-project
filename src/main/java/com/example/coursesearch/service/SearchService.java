package com.example.coursesearch.service;

import com.example.coursesearch.model.CourseDocument;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch.core.search.Hit;

@Service
public class SearchService {
    private final ElasticsearchClient elasticsearchClient;
    private static final String INDEX_NAME = "courses";

    public SearchService(ElasticsearchClient elasticsearchClient) {
        this.elasticsearchClient = elasticsearchClient;
    }

    public record SearchRequest(
            String q,
            Integer minAge,
            Integer maxAge,
            String category,
            String type,
            Double minPrice,
            Double maxPrice,
            Instant startDate,
            String sort,
            int page,
            int size
    ) {
    }

    public record SearchResponse(long total, List<CourseDocument> courses) {
    }

    public SearchResponse search(SearchRequest req) {
        try {
            int from = Math.max(req.page, 0) * Math.max(req.size, 1);
            int size = Math.max(req.size, 1);

            final String sortField;
            final SortOrder sortOrder;
            if (req.sort == null || req.sort.isBlank() || req.sort.equals("upcoming")) {
                sortField = "nextSessionDate";
                sortOrder = SortOrder.Asc;
            } else if (req.sort.equals("priceAsc")) {
                sortField = "price";
                sortOrder = SortOrder.Asc;
            } else if (req.sort.equals("priceDesc")) {
                sortField = "price";
                sortOrder = SortOrder.Desc;
            } else {
                sortField = "nextSessionDate";
                sortOrder = SortOrder.Asc;
            }

            co.elastic.clients.elasticsearch.core.SearchResponse<CourseDocument> response = elasticsearchClient.search(s -> s
                            .index(INDEX_NAME)
                            .query(q -> q.bool(b -> {
                                if (req.q != null && !req.q.isBlank()) {
                                    b.must(m -> m.multiMatch(mm -> mm
                                            .query(req.q.trim())
                                            .fields("title^2", "description")
                                            .fuzziness("AUTO")
                                    ));
                                }
                                if (req.minAge != null) {
                                    b.filter(f -> f.range(r -> r.number(n -> n.field("minAge").gte(req.minAge.doubleValue()))));
                                }
                                if (req.maxAge != null) {
                                    b.filter(f -> f.range(r -> r.number(n -> n.field("maxAge").lte(req.maxAge.doubleValue()))));
                                }
                                if (req.category != null && !req.category.isBlank()) {
                                    b.filter(f -> f.term(t -> t.field("category").value(v -> v.stringValue(req.category))));
                                }
                                if (req.type != null && !req.type.isBlank()) {
                                    b.filter(f -> f.term(t -> t.field("type").value(v -> v.stringValue(req.type))));
                                }
                                if (req.minPrice != null) {
                                    b.filter(f -> f.range(r -> r.number(n -> n.field("price").gte(req.minPrice))));
                                }
                                if (req.maxPrice != null) {
                                    b.filter(f -> f.range(r -> r.number(n -> n.field("price").lte(req.maxPrice))));
                                }
                                if (req.startDate != null) {
                                    b.filter(f -> f.range(r -> r.date(d -> d.field("nextSessionDate").gte(req.startDate.toString()))));
                                }
                                return b;
                            }))
                            .from(from)
                            .size(size)
                            .sort(so -> so.field(f -> f.field(sortField).order(sortOrder))),
                    CourseDocument.class);

            java.util.List<CourseDocument> courses = new java.util.ArrayList<>();
            for (Hit<CourseDocument> hit : response.hits().hits()) {
                CourseDocument doc = hit.source();
                if (doc != null) {
                    courses.add(doc);
                }
            }

            long total = response.hits().total() != null ? response.hits().total().value() : courses.size();
            return new SearchResponse(total, courses);

        } catch (IOException e) {
            throw new RuntimeException("Error executing search", e);
        }
    }

}
