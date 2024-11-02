package ie.tcd.scss.countryinfo.controller;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for CountryController class, verifies region-related functionality.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CountryControllerRegionTest extends BaseCountryControllerTest {
    Logger logger = LoggerFactory.getLogger(CountryControllerRegionTest.class);

    @Test
    public void getCountriesInRegion_forEuropeSortedByPopulation_shouldReturnOrderedList() {
        // Given
        String region = "Europe";
        String sortBy = "population";

        // When
        ResponseEntity<String> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/countries/region/" + region + "/countries?sortBy=" + sortBy,
                String.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<String> countries = JsonPath.parse(response.getBody()).read("$");
        assertThat(countries).contains("Russia", "Germany", "France");
        // Verify order: Russia should be first due to largest population
        assertThat(countries.get(0)).isEqualTo("Russia");
    }

    @Test
    public void getCountriesInRegion_forEuropeSortedByName_shouldReturnOrderedList() {
        // Given
        String region = "Europe";
        String sortBy = "name";

        // When
        ResponseEntity<String> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/countries/region/" + region + "/countries?sortBy=" + sortBy,
                String.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<String> countries = JsonPath.parse(response.getBody()).read("$");
        assertThat(countries).contains("Russia", "Germany", "France");
        // Verify order: Albania should be first if sorted by name
        assertThat(countries.get(0)).isEqualTo("Albania");
    }

    @Test
    public void getCountriesInRegion_forAfricaSortedByName_shouldReturnOrderedList() {
        // Given
        String region = "Africa";
        String sortBy = "name";

        // When
        ResponseEntity<String> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/countries/region/" + region + "/countries?sortBy=" + sortBy,
                String.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<String> countries = JsonPath.parse(response.getBody()).read("$");
        assertThat(countries).contains("Algeria", "Cameroon", "Zimbabwe", "Uganda");
        // Verify order: Algeria should be first if sorted by name
        assertThat(countries.get(0)).isEqualTo("Algeria");
    }

    @Test
    public void getCountriesInRegion_forAfricaSortedByArea_shouldReturnOrderedList() {
        // Given
        String region = "Africa";
        String sortBy = "area";

        // When
        ResponseEntity<String> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/countries/region/" + region + "/countries?sortBy=" + sortBy,
                String.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<String> countries = JsonPath.parse(response.getBody()).read("$");
        assertThat(countries).contains("Algeria", "Cameroon", "Zimbabwe", "Uganda");
        // Verify order: Algeria should be first if sorted by area
        assertThat(countries.get(0)).isEqualTo("Algeria");
    }

    @Test
    public void getCountriesInRegion_forAsiaSortedByArea_shouldReturnOrderedList() {
        // Given
        String region = "Asia";
        String sortBy = "area";

        // When
        ResponseEntity<String> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/countries/region/" + region + "/countries?sortBy=" + sortBy,
                String.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<String> countries = JsonPath.parse(response.getBody()).read("$");
        assertThat(countries).contains("China", "Japan", "Thailand", "India");
        // Verify order: China should be first if sorted by area
        assertThat(countries.get(0)).isEqualTo("China");
    }

    @Test
    public void getCountriesInRegion_withoutSorting_shouldReturnAllCountries() {
        // Given
        String region = "Europe";

        // When
        ResponseEntity<String> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/countries/region/" + region + "/countries",
                String.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<String> countries = JsonPath.parse(response.getBody()).read("$");
        assertThat(countries)
                .isNotEmpty()
                .contains(
                        // "Svalbard and Jan Mayen",
                        // "Åland Islands",
                        // "Isle of Man",
                        "Switzerland",
                        "Hungary",
                        "Italy",
                        "Andorra",
                        "France",
                        "North Macedonia",
                        "Guernsey",
                        "Faroe Islands",
                        "United Kingdom",
                        "Finland",
                        "Greece",
                        "Croatia",
                        "Netherlands",
                        "Liechtenstein",
                        "Monaco",
                        "Montenegro",
                        "Ukraine",
                        "Bulgaria",
                        "Germany",
                        "Sweden",
                        "Russia",
                        "Cyprus",
                        "Bosnia and Herzegovina",
                        "Spain",
                        "Slovenia",
                        "San Marino",
                        "Iceland",
                        "Luxembourg",
                        "Belarus",
                        "Latvia",
                        "Gibraltar",
                        "Denmark",
                        "Czechia",
                        "Estonia",
                        "Romania",
                        "Vatican City",
                        "Austria",
                        "Ireland",
                        "Norway",
                        "Lithuania",
                        "Slovakia",
                        "Moldova",
                        "Jersey",
                        "Malta",
                        "Kosovo",
                        "Albania",
                        "Serbia",
                        "Poland",
                        "Portugal",
                        "Belgium");
    }

    @Test
    public void getCountriesInRegion_forInvalidRegion_shouldReturnNotFound() {
        // Given
        String region = "InvalidRegion";

        // When
        ResponseEntity<String> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/countries/region/" + region + "/countries",
                String.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void getCountriesInRegion_withInvalidSortBy_shouldIgnoreSorting() {
        // Given
        String region = "Europe";
        String sortBy = "invalid";

        // When
        ResponseEntity<String> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/countries/region/" + region + "/countries?sortBy=" + sortBy,
                String.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<String> countries = JsonPath.parse(response.getBody()).read("$");
        assertThat(countries)
                .isNotEmpty()
                .contains("Germany", "France", "Spain", "Italy", "Austria", "Ireland", "Vatican City", "Monaco", "Belgium");
    }

    @Test
    public void getCountriesInRegion_forAsiaSortedByPopulation_shouldReturnOrderedList() {
        // Given
        String region = "Asia";
        String sortBy = "population";

        // When
        ResponseEntity<String> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/countries/region/" + region + "/countries?sortBy=" + sortBy,
                String.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<String> countries = JsonPath.parse(response.getBody()).read("$");
        assertThat(countries).contains("China", "India", "Indonesia", "Pakistan", "Bangladesh", "Japan");
        // Verify order: China and India should be first due to largest populations
        assertThat(countries.get(0)).isEqualTo("China"); // Might no longer be correct, but that's data the external API returns
        assertThat(countries.get(1)).isEqualTo("India");
        assertThat(countries.get(2)).isEqualTo("Indonesia");
        assertThat(countries.get(3)).isEqualTo("Pakistan");
        assertThat(countries.get(4)).isEqualTo("Bangladesh");
        assertThat(countries.get(5)).isEqualTo("Japan");
    }

    @Test
    public void getCountriesInRegion_forOceaniaSortedByName_shouldReturnOrderedList() {
        // Given
        String region = "Oceania";
        String sortBy = "name";

        // When
        ResponseEntity<String> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/countries/region/" + region + "/countries?sortBy=" + sortBy,
                String.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<String> countries = JsonPath.parse(response.getBody()).read("$");
        assertThat(countries).contains("Australia", "New Zealand", "Kiribati", "Fiji", "Marshall Islands", "Micronesia", "Nauru", "American Samoa");
        // Verify order: Australia should be first alphabetically
        assertThat(countries.get(0)).isEqualTo("American Samoa");
        assertThat(countries.get(1)).isEqualTo("Australia");
    }

    @Test
    public void getCountriesInRegion_withEmptyRegionParameter_shouldReturnNotFound() {
        // Given
        String region = "";

        // When
        ResponseEntity<String> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/countries/region/" + region + "/countries",
                String.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void getCountriesInRegion_forAmericasSortedByArea_shouldReturnOrderedList() {
        // Given
        String region = "Americas";
        String sortBy = "area";

        // When
        ResponseEntity<String> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/countries/region/" + region + "/countries?sortBy=" + sortBy,
                String.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<String> countries = JsonPath.parse(response.getBody()).read("$");
        assertThat(countries).contains("Canada", "United States", "Brazil");
        // Verify the largest countries are at the start
        assertThat(countries.subList(0, 3))
                .contains("Canada", "United States", "Brazil");
    }

    @Test
    public void getEuropeStatistics_shouldReturnValidData() {
        // Given
        String region = "Europe";

        // When
        ResponseEntity<String> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/countries/region/" + region + "/statistics",
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        String responseBody = response.getBody();
        assertThat(responseBody).isNotNull();

        // Extract and verify fields
        Long totalPopulation = JsonPath.parse(responseBody).read("$.totalPopulation", Long.class);
        Double totalArea = JsonPath.parse(responseBody).read("$.totalArea");
        Double avgPopulationDensity = JsonPath.parse(responseBody).read("$.averagePopulationDensity");
        Map<String, Integer> languages = JsonPath.parse(responseBody).read("$.mostCommonLanguages");

        assertThat(totalPopulation).isGreaterThan(0);
        assertThat(totalArea).isGreaterThan(0);
        assertThat(avgPopulationDensity).isGreaterThan(0);
        assertThat(languages).isNotEmpty();
    }

}