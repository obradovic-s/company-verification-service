package com.example.companyverification.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

@Component
public class DataFileLoader {

    private final ObjectMapper objectMapper;

    public DataFileLoader(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public <T> List<T> loadList(String fileName, Class<T[]> arrayClass) {
        try {
            Resource resource = new ClassPathResource("data/" + fileName);
            if (!resource.exists()) {
                throw new IllegalStateException("Data file not found on classpath: data/" + fileName);
            }

            try (InputStream inputStream = resource.getInputStream()) {
                return Arrays.asList(objectMapper.readValue(inputStream, arrayClass));
            }
        } catch (IOException ex) {
            throw new IllegalStateException("Could not load data file: " + fileName, ex);
        }
    }
}
