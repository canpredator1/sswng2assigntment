package ie.tcd.scss.countryinfo.controller;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.offset;

/**
 * Tests for CountryController class, verifies comparison functionality.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CountryControllerComparisonTest extends BaseCountryControllerTest {
    Logger logger = LoggerFactory.getLogger(CountryControllerComparisonTest.class);

    @Test
    public void compareGermanyAndFrance_shouldReturnValidComparison() {
        // Given
        String country1 = "Germany";
        String country2 = "France";

        // When
        ResponseEntity<String> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/countries/compare/" + country1 + "/" + country2,
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        String responseBody = response.getBody();
        assertThat(responseBody).isNotNull();

        // Demonstrate use of prettyPrintJson helper method
        String prettyJson = prettyPrintJson(responseBody);
        logger.debug("responseBody: \n{}", prettyJson);

        // Extract and verify fields
        Double populationRatio = JsonPath.parse(responseBody).read("$.populationRatio", Double.class);
        Double areaRatio = JsonPath.parse(responseBody).read("$.areaRatio", Double.class);
        Boolean directlyBordering = JsonPath.parse(responseBody).read("$.directlyBordering");
        List<String> sharedLanguages = JsonPath.parse(responseBody).read("$.sharedLanguages");

        assertThat(populationRatio).isGreaterThan(0); //
        assertThat(populationRatio).isCloseTo(1.24, offset(0.1)); // Germany's population is about 124% that of France

        assertThat(areaRatio).isGreaterThan(0);
        assertThat(areaRatio).isCloseTo(0.65, offset(0.01)); // Germany's area is about 65% that of France

        assertThat(directlyBordering).isTrue();
        assertThat(sharedLanguages).isNotNull();
        assertThat(sharedLanguages).isEmpty();
    }

    @Test
    public void compareGermanyAndAustria_shouldReturnValidComparison() {
        // Given
        String country1 = "Germany";
        String country2 = "Austria";

        // When
        ResponseEntity<String> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/countries/compare/" + country1 + "/" + country2,
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        String responseBody = response.getBody();
        assertThat(responseBody).isNotNull();

        // Demonstrate use of prettyPrintJson helper method
        String prettyJson = prettyPrintJson(responseBody);
        logger.debug("responseBody: \n{}", prettyJson);

        // Extract and verify fields
        Double populationRatio = JsonPath.parse(responseBody).read("$.populationRatio", Double.class);
        Double areaRatio = JsonPath.parse(responseBody).read("$.areaRatio", Double.class);
        Boolean directlyBordering = JsonPath.parse(responseBody).read("$.directlyBordering");
        List<String> sharedLanguages = JsonPath.parse(responseBody).read("$.sharedLanguages");

        assertThat(populationRatio).isGreaterThan(0); //
        assertThat(populationRatio).isCloseTo(9.33, offset(0.01)); // Germany's population is about 933% that of Austria

        assertThat(areaRatio).isGreaterThan(0);
        assertThat(areaRatio).isCloseTo(4.26, offset(0.01)); // Germany's area is about 426% that of Austria

        assertThat(directlyBordering).isTrue();
        assertThat(sharedLanguages).isNotNull();
        assertThat(sharedLanguages).isEqualTo(List.of("German"));
    }

    @Test
    public void compareUnitedKingdomAndUnitedStates_shouldReturnValidComparison() {
        // Given
        String country1 = "United Kingdom";
        String country2 = "United States of America";

        // When
        ResponseEntity<String> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/countries/compare/" + country1 + "/" + country2,
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        String responseBody = response.getBody();
        assertThat(responseBody).isNotNull();

        // Demonstrate use of prettyPrintJson helper method
        String prettyJson = prettyPrintJson(responseBody);
        logger.debug("responseBody: \n{}", prettyJson);

        // Extract and verify fields
        Double populationRatio = JsonPath.parse(responseBody).read("$.populationRatio", Double.class);
        Double areaRatio = JsonPath.parse(responseBody).read("$.areaRatio", Double.class);
        Boolean directlyBordering = JsonPath.parse(responseBody).read("$.directlyBordering");
        List<String> sharedLanguages = JsonPath.parse(responseBody).read("$.sharedLanguages");

        assertThat(populationRatio).isGreaterThan(0); //
        assertThat(populationRatio).isCloseTo(0.20, offset(0.01)); // UK population is about 20% that of USA

        assertThat(areaRatio).isGreaterThan(0);
        assertThat(areaRatio).isCloseTo(0.026, offset(0.01)); // UK area is about 2.6% that of USA

        assertThat(directlyBordering).isFalse();
        assertThat(sharedLanguages).isNotNull();
        assertThat(sharedLanguages).isEqualTo(List.of("English"));
    }

    @Test
    public void compareCountriesWithInvalidCountry_shouldReturnBadRequest() {
        // Given
        String country1 = "Germany";
        String country2 = "InvalidCountry";

        // When
        ResponseEntity<String> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/countries/compare/" + country1 + "/" + country2,
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

}