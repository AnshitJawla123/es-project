package com.example.coursesearch.model;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

public class CourseDocumentTest {

    @Test
    void setters_and_getters_work_and_titleSuggest_follows_title() {
        CourseDocument cd = new CourseDocument();
        cd.setId("id");
        cd.setTitle("Title");
        cd.setDescription("Desc");
        cd.setCategory("Cat");
        cd.setType("COURSE");
        cd.setGradeRange("7th-9th");
        cd.setMinAge(10);
        cd.setMaxAge(12);
        cd.setPrice(9.99);
        Instant now = Instant.parse("2025-01-01T00:00:00Z");
        cd.setNextSessionDate(now);

        assertThat(cd.getTitleSuggest()).isEqualTo("Title");
        assertThat(cd.getNextSessionDate()).isEqualTo(now);
    }
}


