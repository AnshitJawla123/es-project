package com.example.coursesearch.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ElasticsearchConfigTest {

    @Test
    void objectMapper_hasJavaTimeModule_and_notTimestamps() {
        ElasticsearchConfig cfg = new ElasticsearchConfig();
        ObjectMapper om = cfg.objectMapper();
        assertThat(om.getRegisteredModuleIds()).isNotEmpty();
        assertThat(om.isEnabled(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)).isFalse();
    }
}


