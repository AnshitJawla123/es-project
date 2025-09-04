package com.example.coursesearch.service;

import com.example.coursesearch.model.CourseDocument;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.search.Hit;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

@Service
public class AutocompleteService {
	private final ElasticsearchClient elasticsearchClient;
	private static final String INDEX_NAME = "courses";

	public AutocompleteService(ElasticsearchClient elasticsearchClient) {
		this.elasticsearchClient = elasticsearchClient;
	}

	public List<String> getSuggestions(String prefix) {
		if (prefix == null || prefix.trim().isEmpty()) {
			return List.of();
		}
		
		try {
			var response = elasticsearchClient.search(s -> s
				.index(INDEX_NAME)
				.size(10)
				.query(q -> q.bool(b -> b
					.should(sh -> sh.multiMatch(mm -> mm
						.query(prefix.trim())
						.fields("title^3", "description")
						.fuzziness("AUTO")
						.prefixLength(1)
					))
					.should(sh -> sh.wildcard(w -> w
						.field("title")
						.value("*" + prefix.trim() + "*")
					))
					.should(sh -> sh.wildcard(w -> w
						.field("description")
						.value("*" + prefix.trim() + "*")
					))
					.minimumShouldMatch("1")
				)),
				CourseDocument.class);

			LinkedHashSet<String> titles = new LinkedHashSet<>();
			for (Hit<CourseDocument> hit : response.hits().hits()) {
				CourseDocument doc = hit.source();
				if (doc != null && doc.getTitle() != null) {
					titles.add(doc.getTitle());
				}
			}
			return new ArrayList<>(titles);
		} catch (IOException e) {
			throw new RuntimeException("Error executing autocomplete search", e);
		}
	}
}
