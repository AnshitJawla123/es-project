package com.example.coursesearch.service;

import com.example.coursesearch.model.CourseDocument;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.bulk.BulkOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {
	private static final Logger log = LoggerFactory.getLogger(DataLoader.class);
	private final ElasticsearchClient elasticsearchClient;
	private final ObjectMapper objectMapper;
	private static final String INDEX_NAME = "courses";

	public DataLoader(ElasticsearchClient elasticsearchClient, ObjectMapper objectMapper) {
		this.elasticsearchClient = elasticsearchClient;
		this.objectMapper = objectMapper;
	}

	@Override
	public void run(String... args) throws Exception {
		// Check if index exists
		boolean exists = elasticsearchClient.indices().exists(e -> e.index(INDEX_NAME)).value();

		if (!exists) {
			// Create index with mapping
			String mappingJson = """
                {
                  "mappings": {
                    "properties": {
                      "id": {"type": "keyword"},
                      "title": {"type": "text", "analyzer": "standard"},
                      "titleSuggest": {"type": "completion"},
                      "description": {"type": "text", "analyzer": "standard"},
                      "category": {"type": "keyword"},
                      "type": {"type": "keyword"},
                      "gradeRange": {"type": "keyword"},
                      "minAge": {"type": "integer"},
                      "maxAge": {"type": "integer"},
                      "price": {"type": "double"},
                      "nextSessionDate": {"type": "date"}
                    }
                  }
                }
                """;

			elasticsearchClient.indices().create(c -> c.index(INDEX_NAME).withJson(new java.io.StringReader(mappingJson)));
			log.info("Created index '{}'", INDEX_NAME);
		}

		// Load and index courses
		InputStream is = new ClassPathResource("sample-courses.json").getInputStream();
		List<CourseDocument> courses = objectMapper.readValue(is, new TypeReference<List<CourseDocument>>(){});

		// Ensure completion field is populated
		for (CourseDocument course : courses) {
			if (course.getTitle() != null) {
				course.setTitleSuggest(course.getTitle());
			}
		}

		// Bulk index courses
		java.util.List<BulkOperation> ops = new java.util.ArrayList<>();
		for (CourseDocument course : courses) {
			ops.add(BulkOperation.of(op -> op
				.index(idx -> idx
					.index(INDEX_NAME)
					.id(course.getId())
					.document(course)
				)
			));
		}

		elasticsearchClient.bulk(BulkRequest.of(b -> b.operations(ops)));
		log.info("Indexed {} courses", courses.size());
	}
}
