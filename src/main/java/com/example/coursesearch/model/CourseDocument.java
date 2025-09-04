package com.example.coursesearch.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.Instant;

public class CourseDocument {
	@JsonProperty("id")
	private String id;

	@JsonProperty("title")
	private String title;

	@JsonProperty("titleSuggest")
	private String titleSuggest;

	@JsonProperty("description")
	private String description;

	@JsonProperty("category")
	private String category;

	@JsonProperty("type")
	private String type; // ONE_TIME, COURSE, CLUB

	@JsonProperty("gradeRange")
	private String gradeRange;

	@JsonProperty("minAge")
	private Integer minAge;

	@JsonProperty("maxAge")
	private Integer maxAge;

	@JsonProperty("price")
	private Double price;

	@JsonProperty("nextSessionDate")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssX", timezone = "UTC")
	private Instant nextSessionDate;

	public CourseDocument() {}

	public String getId() { return id; }
	public void setId(String id) { this.id = id; }
	public String getTitle() { return title; }
	public void setTitle(String title) { 
		this.title = title; 
		this.titleSuggest = title; // Set completion field to same value
	}
	public String getTitleSuggest() { return titleSuggest; }
	public void setTitleSuggest(String titleSuggest) { this.titleSuggest = titleSuggest; }
	public String getDescription() { return description; }
	public void setDescription(String description) { this.description = description; }
	public String getCategory() { return category; }
	public void setCategory(String category) { this.category = category; }
	public String getType() { return type; }
	public void setType(String type) { this.type = type; }
	public String getGradeRange() { return gradeRange; }
	public void setGradeRange(String gradeRange) { this.gradeRange = gradeRange; }
	public Integer getMinAge() { return minAge; }
	public void setMinAge(Integer minAge) { this.minAge = minAge; }
	public Integer getMaxAge() { return maxAge; }
	public void setMaxAge(Integer maxAge) { this.maxAge = maxAge; }
	public Double getPrice() { return price; }
	public void setPrice(Double price) { this.price = price; }
	public Instant getNextSessionDate() { return nextSessionDate; }
	public void setNextSessionDate(Instant nextSessionDate) { this.nextSessionDate = nextSessionDate; }
}
