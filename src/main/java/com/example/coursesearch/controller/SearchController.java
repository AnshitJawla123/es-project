package com.example.coursesearch.controller;

import com.example.coursesearch.model.CourseDocument;
import com.example.coursesearch.service.SearchService;
import com.example.coursesearch.service.AutocompleteService;
import java.time.Instant;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/search")
public class SearchController {
	private final SearchService service;
	private final AutocompleteService autocompleteService;
	
	public SearchController(SearchService service, AutocompleteService autocompleteService) { 
		this.service = service; 
		this.autocompleteService = autocompleteService;
	}

	@GetMapping
	public SearchService.SearchResponse search(
		@RequestParam(required = false) String q,
		@RequestParam(required = false) Integer minAge,
		@RequestParam(required = false) Integer maxAge,
		@RequestParam(required = false) String category,
		@RequestParam(required = false) String type,
		@RequestParam(required = false) Double minPrice,
		@RequestParam(required = false) Double maxPrice,
		@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startDate,
		@RequestParam(defaultValue = "upcoming") String sort,
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size
	) {
		SearchService.SearchRequest req = new SearchService.SearchRequest(q, minAge, maxAge, category, type, minPrice, maxPrice, startDate, sort, page, size);
		return service.search(req);
	}

	@GetMapping("/suggest")
	public List<String> suggest(@RequestParam String q) {
		return autocompleteService.getSuggestions(q);
	}
}
