package com.example.coursesearch.service;

import com.example.coursesearch.model.CourseDocument;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.HitsMetadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SuppressWarnings({"unchecked", "rawtypes"})
public class AutocompleteServiceTest {

    private ElasticsearchClient client;
    private AutocompleteService service;

    @BeforeEach
    void setUp() {
        client = Mockito.mock(ElasticsearchClient.class);
        service = new AutocompleteService(client);
    }

    @Test
    void suggest_returnsEmpty_onNullOrBlank() {
        assertThat(service.getSuggestions(null)).isEmpty();
        assertThat(service.getSuggestions("   ")).isEmpty();
    }

    @Test
    void suggest_wrapsIOException() throws IOException {
        Mockito.when(client.search(Mockito.<java.util.function.Function>any(), Mockito.eq((java.lang.reflect.Type) CourseDocument.class)))
            .thenThrow(new IOException("nope"));
        assertThrows(RuntimeException.class, () -> service.getSuggestions("x"));
    }
}


