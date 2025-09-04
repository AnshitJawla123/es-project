package com.example.coursesearch.controller;

import com.example.coursesearch.model.CourseDocument;
import com.example.coursesearch.service.AutocompleteService;
import com.example.coursesearch.service.SearchService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(controllers = SearchController.class)
public class SearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SearchService searchService;

    @MockBean
    private AutocompleteService autocompleteService;

    @Test
    void search_endpoint_returnsResult() throws Exception {
        CourseDocument cd = new CourseDocument();
        cd.setId("1");
        cd.setTitle("Data Structures #0");
        SearchService.SearchResponse resp = new SearchService.SearchResponse(1, java.util.List.of(cd));
        when(searchService.search(any(SearchService.SearchRequest.class))).thenReturn(resp);

        mockMvc.perform(get("/api/search")
                .param("q", "Data")
                .param("minAge", "10")
                .param("maxAge", "15")
                .param("category", "Music")
                .param("type", "ONE_TIME")
                .param("minPrice", "100")
                .param("maxPrice", "300")
                .param("startDate", Instant.parse("2025-01-01T00:00:00Z").toString())
                .param("sort", "priceAsc")
                .param("page", "0")
                .param("size", "10")
        ).andExpect(status().isOk())
         .andExpect(jsonPath("$.total").value(1));
    }

    @Test
    void suggest_endpoint_returnsList() throws Exception {
        when(autocompleteService.getSuggestions("Phys")).thenReturn(List.of("Physics Fun #4"));
        mockMvc.perform(get("/api/search/suggest").param("q", "Phys"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("Physics Fun #4"));
    }

    @Test
    void search_endpoint_defaults_when_missingParams() throws Exception {
        SearchService.SearchResponse resp = new SearchService.SearchResponse(0, java.util.List.of());
        when(searchService.search(any(SearchService.SearchRequest.class))).thenReturn(resp);

        mockMvc.perform(get("/api/search"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.total").value(0));
    }
}


