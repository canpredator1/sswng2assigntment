package ie.tcd.scss.countryinfo.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;

/**
 * Base class for controller tests. Contains common setup and helper methods.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BaseCountryControllerTest {
    @LocalServerPort
    protected int port;

    @Autowired
    protected TestRestTemplate restTemplate;

    /**
     * Helper method to pretty print JSON
     * @param json JSON string
     * @return pretty printed JSON
     */
    protected static String prettyPrintJson(String json) {
        ObjectMapper mapper = new ObjectMapper();
        String prettyJson = null;
        try {
            JsonNode jsonNode = mapper.readTree(json);
            prettyJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonNode);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return prettyJson;
    }

}
