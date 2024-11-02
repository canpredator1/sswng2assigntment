package ie.tcd.scss.countryinfo.controller;

import ie.tcd.scss.countryinfo.domain.Country;

import com.jayway.jsonpath.JsonPath;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.offset;

/**
 * Tests for CountryController class, verifies basic functionality for retrieving country information.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CountryControllerTest extends BaseCountryControllerTest {
    Logger logger = LoggerFactory.getLogger(CountryControllerTest.class);

    @Test
    public void getGermany_shouldReturnSelectedAttributes_JsonPath() {
        // Given the country name "Germany"
        String countryName = "Germany";

        // When making a GET request to /countries/Germany
        ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:" + port + "/countries/" + countryName, String.class);

        // Then the response should be 200 OK and contain selected attributes
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        // extract fields from body (which is a string containing JSON)
        String responseBody = response.getBody();
        String commonName = JsonPath.parse(responseBody).read("$.name.common");
        List<String> capitals = JsonPath.parse(responseBody).read("$.capital");
        List<Double> latlng = JsonPath.parse(responseBody).read("$.latlng");
        List<String> continents = JsonPath.parse(responseBody).read("$.continents");

        // Assertions
        assertThat(commonName).isEqualTo("Germany");
        assertThat(capitals).containsExactly("Berlin");
        assertThat(latlng).containsExactly(51.0, 9.0);
        assertThat(continents).containsExactly("Europe");
    }

    @Test
    public void getGermany_shouldReturnSelectedAttributes_DomainObjects() {
        // Given the country name "Germany"
        String countryName = "Germany";

        // When making a GET request to /countries/Germany
        ResponseEntity<Country> response = restTemplate.getForEntity("http://localhost:" + port + "/countries/" + countryName, Country.class);

        // Then the response should be 200 OK and contain selected attributes
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        // extract fields from Country object
        Country country = response.getBody();
        String commonName = country.getName().getCommon();
        List<String> capitals = country.getCapital();
        List<Double> latlng = country.getLatlng();
        List<String> continents = country.getContinents();

        // Assertions
        assertThat(commonName).isEqualTo("Germany");
        assertThat(capitals).containsExactly("Berlin");
        assertThat(latlng).containsExactly(51.0, 9.0);
        assertThat(continents).containsExactly("Europe");

    }


    @Test
    public void getFrance_shouldReturnSelectedAttributes() {
        // Given the country name "Germany"
        String countryName = "France";

        // When making a GET request to /countries/France
        ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:" + port + "/countries/" + countryName, String.class);

        // Then the response should be 200 OK and contain selected attributes
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        // extract fields from body
        String responseBody = response.getBody();
        String commonName = JsonPath.parse(responseBody).read("$.name.common");
        List<String> capitals = JsonPath.parse(responseBody).read("$.capital");
        List<Double> latlng = JsonPath.parse(responseBody).read("$.latlng");
        List<String> continents = JsonPath.parse(responseBody).read("$.continents");

        // Assertions
        assertThat(commonName).isEqualTo("France");
        assertThat(capitals).containsExactly("Paris");
        assertThat(latlng).containsExactly(46.0, 2.0);
        assertThat(continents).containsExactly("Europe");
    }


    @Test
    public void getSouthAfrica_shouldReturnSelectedAttributes() {
        // Given the country name "South Africa"
        String countryName = "South Africa"; // escaping the space as %20 will lead to 404 Not Found

        // When making a GET request to /countries/South%20Africa
        ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:" + port + "/countries/" + countryName, String.class);

        // Then the response should be 200 OK and contain selected attributes
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        // extract fields from body
        String responseBody = response.getBody();
        String commonName = JsonPath.parse(responseBody).read("$.name.common");
        List<String> capitals = JsonPath.parse(responseBody).read("$.capital");
        List<Double> latlng = JsonPath.parse(responseBody).read("$.latlng");
        List<String> continents = JsonPath.parse(responseBody).read("$.continents");

        // Assertions
        assertThat(commonName).isEqualTo("South Africa");
        assertThat(capitals).containsExactlyInAnyOrder("Pretoria", "Cape Town", "Bloemfontein");
        assertThat(latlng).containsExactly(-29.0, 24.0);
        assertThat(continents).containsExactly("Africa");
    }

    @Test
    public void getContinentsForFrance_shouldReturnEurope() {
        // Given the country name "France"
        String countryName = "France";

        // When making a GET request to /countries/France/continents/
        ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:" + port + "/countries/" + countryName + "/continents", String.class);

        // Then the response should be 200 OK and contain selected attributes
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Europe");
    }

    @Test
    public void getContinentsForRussia_shouldReturnEuropeAsia() {
        // Given the country name "Russia"
        String countryName = "Russia";

        // When making a GET request to /countries/Russia/continents/
        ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:" + port + "/countries/" + countryName + "/continents",String.class);

        // Then the response should be 200 OK and contain selected attributes
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Europe, Asia");
    }

    @Test
    public void getFlagForFrance_shouldReturnFlagUrl() {
        // Given the country name "France"
        String countryName = "France";

        // When making a GET request to /countries/France/flag/
        ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:" + port + "/countries/" + countryName + "/flag", String.class);

        // Then the response should be 200 OK and contain the flag URL
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("https://flagcdn.com/w320/fr.png");
    }

    @Test
    public void getMapForFrance_shouldReturnMapUrl() {
        // Given the country name "Bolivia"
        String countryName = "Bolivia";

        // When making a GET request to /countries/Bolivia/map/
        ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:" + port + "/countries/" + countryName + "/map", String.class);

        // Then the response should be 200 OK and contain the flag URL
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("https://goo.gl/maps/9DfnyfbxNM2g5U9b9");
    }

    @Test
    public void translationForFranceToGerman_shouldReturnFrankreich() {
        // Given the country name "France"
        String countryName = "France";
        String languageId = "deu";

        // When making a GET request to /countries/France/translation/ger/
        ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:" + port + "/countries/" + countryName + "/translation/" + languageId, String.class);

        // Then the response should be 200 OK and contain the flag URL
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Frankreich");
    }

    @Test
    public void translationForFranceToJapanese_shouldReturnJapaneseTranslation() {
        // Given the country name "France"
        String countryName = "France";
        String languageId = "jpn";

        // When making a GET request to /countries/France/translation/ger/
        ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:" + port + "/countries/" + countryName + "/translation/" + languageId, String.class);

        // Then the response should be 200 OK and contain the flag URL
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("フランス");
    }

    @Test
    public void translationForInvalidLanguage_shouldReturnNotFound() {
        // Given the country name "France"
        String countryName = "France";
        String languageId = "invalid-language-id";

        // When making a GET request to /countries/France/translation/invalid-language-id/
        ResponseEntity<String> response =
                restTemplate.getForEntity("http://localhost:" + port + "/countries/" + countryName + "/translation/" + languageId, String.class);

        // Then the response should be 200 OK without a response body
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
    }

    @Test
    public void getCountryInfo_withInvalidCountryName_shouldReturnNotFound() {
        // Given an invalid country name
        String countryName = "InvalidCountry";

        // When making a GET request to /country
        ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:" + port + "/country?name=" + countryName, String.class);

        // Then the response should be 404 Not Found
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void getNonExistentEndpoint_shouldReturnNotFound() {
        // Given a non-existent endpoint
        String endpoint = "/nonexistent";

        // When making a GET request to /countries/nonexistent
        ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:" + port + endpoint, String.class);

        // Then the response should be 404 Not Found
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void getMostPopulousCountriesWithPopulation_withStan_shouldReturnOrderedListWithPopulations() {
        // Given
        String substring = "stan";

        // When
        ResponseEntity<String> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/countries/" + substring + "/mostPopulousWithPopulation",
                String.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        String[] countries = response.getBody().split("; ");

        // Verify format and order
        // Pakistan
        // Afghanistan
        // Kazakhstan
        // Uzbekistan
        // Kyrgyzstan
        // Tajikistan
        // Turkmenistan
        // Saint Helena, Ascension and Tristan da Cunha
        assertThat(countries).hasSize(8);

        assertThat(countries[0]).startsWith("Pakistan (");
        assertThat(countries[0]).endsWith(")");
        assertThat(countries[1]).startsWith("Afghanistan (");
        assertThat(countries[1]).endsWith(")");
        assertThat(countries[2]).startsWith("Uzbekistan (");
        assertThat(countries[1]).endsWith(")");

        // Extract and verify populations are in descending order
        long previousPopulation = Long.MAX_VALUE;
        for (String country : countries) {
            String populationStr = country.substring(country.indexOf("(") + 1, country.indexOf(")"));
            long population = Long.parseLong(populationStr);
            assertThat(population).isLessThanOrEqualTo(previousPopulation);
            previousPopulation = population;
        }

        // Log response for debugging
        logger.info("Countries with population: {}", response.getBody());
    }


    @Test
    public void getMostPopulousCountries_withNoMatchingCountries_shouldReturnNotFound() {
        // Given a substring that matches no countries
        String substring = "Xyz";

        // When making a GET request to /countries/Xyz/mostPopulous/
        ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:" + port + "/countries/" + substring + "/mostPopulous", String.class);

        // Then the response should be 404 Not Found
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void getDemographics_forGermany_shouldReturnCorrectData() {
        // Given
        String countryName = "Germany";

        // When
        ResponseEntity<String> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/countries/" + countryName + "/demographics",
                String.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        String responseBody = response.getBody();
        int population = JsonPath.parse(responseBody).read("$.population");
        int area = JsonPath.parse(responseBody).read("$.area");
        Map<String, String> languages = JsonPath.parse(responseBody).read("$.languages");

        assertThat(population).isGreaterThan(80000000);
        assertThat(area).isGreaterThan(300000);
        assertThat(languages).containsKey("deu");
    }

    @Test
    public void getBorderingCountries_forFrance_shouldReturnAllNeighbors() {
        // Given
        String countryName = "France";

        // When
        ResponseEntity<String> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/countries/" + countryName + "/borders",
                String.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<String> borders = JsonPath.parse(response.getBody()).read("$");
        assertThat(borders).contains("ESP", "AND", "DEU", "ITA", "CHE", "BEL", "LUX");
    }

    @Test
    public void getDemographics_forInvalidCountry_shouldReturnNotFound() {
        // Given
        String countryName = "InvalidCountry";

        // When
        ResponseEntity<String> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/countries/" + countryName + "/demographics",
                String.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void getRegionStatistics_Europe_withPrettyFormat_shouldReturnFormattedNumbers() {
        // Given
        String region = "Europe";

        // When making a GET request to /countries/region/Europe/statistics with pretty format
        ResponseEntity<String> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/countries/region/" + region + "/statistics?format=pretty",
                String.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        String responseBody = response.getBody();
        String totalPopulation = JsonPath.parse(responseBody).read("$.totalPopulation");
        String totalArea = JsonPath.parse(responseBody).read("$.totalArea");
        Map<String, Object> mostCommonLanguages = JsonPath.parse(responseBody).read("$.mostCommonLanguages");

        // Verify formatted numbers contain commas
        assertThat(totalPopulation).containsPattern("\\d+,\\d+");
        assertThat(totalArea).containsPattern("\\d+,\\d+");

        // Verify the structure and content
        assertThat(mostCommonLanguages).isNotEmpty();
        assertThat(mostCommonLanguages).containsKey("English");
        assertThat(mostCommonLanguages).containsKey("French");

        // Log the response for debugging
        logger.info("Pretty formatted response: {}", responseBody);
    }

    @Test
    public void getRegionStatistics_Europe_withoutPrettyFormat_shouldReturnRawNumbers() {
        // Given
        String region = "Europe";

        // When making a GET request to /countries/region/Europe/statistics without pretty format
        ResponseEntity<String> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/countries/region/" + region + "/statistics",
                String.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        String responseBody = response.getBody();
        Number totalPopulation = JsonPath.parse(responseBody).read("$.totalPopulation");
        Number totalArea = JsonPath.parse(responseBody).read("$.totalArea");
        Number averagePopulationDensity = JsonPath.parse(responseBody).read("$.averagePopulationDensity");
        Map<String, Object> mostCommonLanguages = JsonPath.parse(responseBody).read("$.mostCommonLanguages");
        Map<String, Object> mostCommonCurrencies = JsonPath.parse(responseBody).read("$.mostCommonCurrencies");

        // Verify raw numbers
        assertThat(totalPopulation.longValue()).isGreaterThan(700000000L); // Europe's population should be more than 700M
        assertThat(totalPopulation.doubleValue()).isCloseTo(747E6, Offset.offset(10E6)); // Europe's population should be around 747M
        assertThat(totalArea.doubleValue()).isGreaterThan(10000000.0); // Europe's area should be more than 10M km²
        assertThat(totalArea.doubleValue()).isCloseTo(23.1E6, Offset.offset(1E6)); // Europe's population should be around 23M km²
        assertThat(averagePopulationDensity.doubleValue()).isGreaterThan(0.0);

        // Verify the structure and content
        assertThat(mostCommonLanguages).isNotEmpty();
        assertThat(mostCommonCurrencies).isNotEmpty();

        // Log the response for debugging
        logger.info("Raw number response: {}", responseBody);
    }

}