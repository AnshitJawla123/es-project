package com.example.coursesearch.service;

import com.example.coursesearch.model.CourseDocument;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.search.TotalHits;
import co.elastic.clients.elasticsearch.core.search.TotalHitsRelation;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.HitsMetadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SuppressWarnings({"unchecked", "rawtypes"})
public class SearchServiceTest {

    private ElasticsearchClient client;
    private SearchService service;

    @BeforeEach
    void setUp() {
        client = Mockito.mock(ElasticsearchClient.class);
        service = new SearchService(client);
    }
    @Test
    void search_wrapsIOException_inRuntimeException() throws IOException {
        when(client.search(Mockito.<java.util.function.Function>any(), Mockito.eq((java.lang.reflect.Type) CourseDocument.class)))
            .thenThrow(new IOException("boom"));

        SearchService.SearchRequest req = new SearchService.SearchRequest(
            null, null, null, null, null, null, null, null,
            null, 0, 10
        );
        assertThrows(RuntimeException.class, () -> service.search(req));
    }
}


