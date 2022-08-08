package org.dw;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AppTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private final static OpenSearchTestContainer openSearchTestContainer = new OpenSearchTestContainer();

    static {
        openSearchTestContainer.start();
    }

    @DynamicPropertySource
    static void openSearchProperties(DynamicPropertyRegistry registry) {
        registry.add("opensearch.port", openSearchTestContainer::getFirstMappedPort);
    }

    @Test
    void testOpenSearch() {
        // Add data to database
        restTemplate.put("http://localhost:" + port + "/insert/1?name=joe&age=50", null);

        // read the data from the database
        ParameterizedTypeReference<HashMap<String, Object>> responseType = new ParameterizedTypeReference<>() {};
        RequestEntity<Void> request = RequestEntity.get("http://localhost:" + port + "/search/1")
                .accept(MediaType.APPLICATION_JSON).build();
        Map<String, Object> result = restTemplate.exchange(request, responseType).getBody();

        // assert result
        assertNotNull(result);
        assertEquals("joe", result.get("name"));
        assertEquals("50", result.get("age"));
    }

}
