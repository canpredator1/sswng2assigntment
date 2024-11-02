package ie.tcd.scss.countryinfo.controller;
import org.springframework.beans.factory.annotation.Autowired;

        import ie.tcd.scss.countryinfo.domain.Country;
        import ie.tcd.scss.countryinfo.service.CountryService;
        import io.swagger.v3.oas.annotations.Operation;
        import io.swagger.v3.oas.annotations.Parameter;
        import io.swagger.v3.oas.annotations.media.ExampleObject;
        import io.swagger.v3.oas.annotations.tags.Tag;
        import org.springframework.http.HttpStatus;
        import org.springframework.http.ResponseEntity;
        import org.springframework.web.bind.annotation.*;

        import org.slf4j.Logger;
        import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
        import java.util.HashMap;
        import java.util.List;
        import java.util.Map;
        import java.util.stream.Collectors;
        import java.util.*;



@RestController
@RequestMapping("/countries")
@Tag(name = "Country Information", description = "APIs for retrieving and comparing country information")
// Annotation for OpenAPI/Swagger
// See configuration in class OpenApiConfig in package config
// Access at http://localhost:8080/swagger-ui.html
public class CountryController {
    private static final Logger logger = LoggerFactory.getLogger(CountryController.class);
    private static final String API_URL_ALL = "https://restcountries.com/v3.1/all"; // Define the API URL


    private final CountryService countryService;
    private final RestTemplate restTemplate;

    @Autowired
    public CountryController(CountryService countryService, RestTemplate restTemplate) {
        this.countryService = countryService;
        this.restTemplate = restTemplate;
    }


    @Operation(
            summary = "Example method for logging",
            description = "Demonstrates different logging levels and logging with parameters and exceptions"
    )
    @GetMapping("/logging-example")
    public void exampleMethod() {
        // Different logging levels
        logger.trace("Trace Message");  // Not printed by default
        logger.debug("Debug Message");  // Not printed by default
        logger.info("Info Message");    // Printed by default
        logger.warn("Warning Message"); // Printed by default
        logger.error("Error Message");  // Printed by default

        // Logging with parameters
        String param = "test";
        logger.info("Processing request with parameter: {}", param);

        // Logging exceptions
        try {
            throw new Exception("Test exception");
        } catch (Exception e) {
            logger.error("Error occurred", e);
        }
    }


    @Operation(
            summary = "Get country information",
            description = "Retrieves detailed information about a specific country by its name"
    )
    @GetMapping("/{countryname}")
    public ResponseEntity<Country> getCountryInfo(
            @Parameter(
                    description = "The name of the country to retrieve information for",
                    example = "France",
                    required = true
            )
            @PathVariable String countryname
    ) {
        if(countryname.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Country country = countryService.getCountryInfo(countryname);
        if (country == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(country);
    }


    @Operation(
            summary = "Get country flag",
            description = "Retrieves the flag of a specific country by its name"
    )
    @GetMapping("/{countryname}/flag")
    public ResponseEntity<String> getCountryFlag(
            @Parameter(
                    description = "The name of the country to retrieve information for",
                    example = "France",
                    required = true
            )
            @PathVariable String countryname
    ) {
        String flag = countryService.getFlagForCountry(countryname);
        if (flag == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(flag);
    }


    @Operation(
            summary = "Get country map",
            description = "Retrieves the map of a specific country by its name"
    )
    @GetMapping("/{countryname}/map")
    public ResponseEntity<String> getCountryMap(
            @Parameter(
                    description = "The name of the country to retrieve information for",
                    example = "France",
                    required = true
            )
            @PathVariable String countryname
    ) {
        String maps = countryService.getMapForCountry(countryname);
        if (maps == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(maps);
    }


    @Operation(
            summary = "Get continents of a country",
            description = "Retrieves the continents that a specific country is located on"
    )
    @GetMapping("/{countryname}/continents")
    public ResponseEntity<String> getCountryContinents(
            @Parameter(
                    description = "The name of the country to retrieve information for",
                    example = "France",
                    required = true
            )
            @PathVariable String countryname
    ) {
        List<String> continents = countryService.getContinentsForCountry(countryname);
        if (continents == null || continents.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(String.join(", ", continents));
    }


    /**
     * "Returns the names of the countries that contain the given substring in descending order of population"
     * @param substring The substring to search for in country names
     * @return List of country names, separated by semicolon and space for instance
     * Country1; Country2; Country3
     */
    @Operation(
            summary = "Get countries containing substring, sorted by population",
            description = "Returns the names of the countries that contain the given substring in descending order of population"
    )
    @GetMapping("/{substring}/mostPopulous")
    public ResponseEntity<String> getMostPopulousCountries(
            @Parameter(
                    description = "The substring to search for in country names",
                    example = "stan",
                    required = true
            )
            @PathVariable String substring
    ) {
        logger.info("Getting most populous countries with substring: {}", substring);
        List<String> countries = countryService.getNamesMostPopulousCountries(substring);
        if (countries == null || countries.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(String.join("; ", countries));
    }

    /**
     * "Returns the names of the countries that contain the given substring in descending order of population, along with their population."
     * @param substring The substring to search for in country names
     * @return List of country names with population, each value wrapped in quotes and separated by comma and space for instance
     * Country1 (Population1); Country2 (Population2); Country3 (Population3)
     */
    @Operation(
            summary = "Get countries containing substring, including population size, sorted by population",
            description = "Returns the names of the countries that contain the given substring in descending order of population, along with their population."

    )

    @GetMapping("/{substring}/mostPopulousWithPopulation")

    public ResponseEntity<String> getMostPopulousCountriesWithPopulation(
            @Parameter(
                    description = "The substring to search for in country names",
                    example = "stan",
                    required = true
            )
            @PathVariable String substring
    ) {

        if (substring == null || substring.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid substring input");
        }

        Country[] countries = restTemplate.getForObject(API_URL_ALL, Country[].class);
        if (countries == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error retrieving countries");
        }

        // Filter, sort, and format the countries
        List<String> formattedCountries = Arrays.stream(countries)
                .filter(country -> country.getName().getCommon().toLowerCase().contains(substring.toLowerCase())) // Case-insensitive substring search
                .sorted((c1, c2) -> Long.compare(c2.getPopulation(), c1.getPopulation())) // Sort by population, descending
                .map(country -> String.format("%s (%d)", country.getName().getCommon(), country.getPopulation())) // Format as "Name (Population)"
                .collect(Collectors.toList());

        if (formattedCountries.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No countries found with the given substring");
        }

        // Join the formatted strings with "; " separator
        String result = String.join("; ", formattedCountries);
        return ResponseEntity.ok(result);
    }

    @Operation(
            summary = "Get country name translation",
            description = "Retrieves the translation of a specific country name in a given language"
    )
    @GetMapping("/{countryname}/translation/{language}")
    public ResponseEntity<String> getCountryNameTranslation(
            @Parameter(
                    description = "The name of the country to translate",
                    example = "France",
                    required = true
            )
            @PathVariable String countryname,
            @Parameter(
                    description = "The language to translate the country name to",
                    example = "Spanish",
                    required = true
            )
            @PathVariable String language
    ) {
        String translation = countryService.getTranslationForCountry(countryname, language);
        if (translation == null) {
            return ResponseEntity.notFound().build(); // no translation found
        }
        return ResponseEntity.ok(translation);
    }

    @Operation(
            summary = "Get country demographics",
            description = "Retrieves demographic information about a specific country, in particular its population, area, languages, currencies, and timezones"
    )
    @GetMapping("/{countryname}/demographics")
    public ResponseEntity<Map<String, Object>> getCountryDemographics(
            @Parameter(
                    description = "The name of the country to retrieve information for",
                    example = "France",
                    required = true
            )
            @PathVariable String countryname
    ) {
        // Retrieve the country information using CountryService
        Country country = countryService.getCountryInfo(countryname);

        // Check if the country was found
        if (country == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Country not found"));
        }

        // Extract demographic information
        Map<String, Object> demographics = new HashMap<>();
        demographics.put("population", country.getPopulation());
        demographics.put("area", country.getArea());
        demographics.put("languages", country.getLanguages());
        demographics.put("currencies", country.getCurrencies());
        demographics.put("timezones", country.getTimezones());

        // Return the demographic information
        return ResponseEntity.ok(demographics);
    }

    @Operation(
            summary = "Get bordering countries",
            description = "Retrieves the countries that share a border with a specific country"
    )
    @GetMapping("/{countryname}/borders")
    public ResponseEntity<List<String>> getBorderingCountries(
            @Parameter(
                    description = "The name of the country to retrieve information for",
                    example = "France",
                    required = true
            )
            @PathVariable String countryname
    ) {
        // Retrieve the country information using CountryService
        Country country = countryService.getCountryInfo(countryname);

        // Check if the country was found
        if (country == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(List.of("Country not found"));
        }

        // Get the list of bordering country codes
        List<String> borderCodes = country.getBorders();

        // Check if the country has no borders or if the list is empty
        if (borderCodes == null || borderCodes.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(List.of("No bordering countries found"));
        }

        // Retrieve the names of the bordering countries
        List<String> borderingCountries = borderCodes.stream()
                .map(code -> countryService.getCountryInfo(code)) // Get Country objects by border code
                .filter(Objects::nonNull) // Filter out nulls in case a country isn't found
                .map(borderCountry -> borderCountry.getName().getCommon()) // Get the common name of each country
                .collect(Collectors.toList());

        // Return the list of bordering country names
        return ResponseEntity.ok(borderingCountries);
    }

    @Operation(
            summary = "Get countries in region",
            description = "Retrieves the names of the countries in a specific region, optionally sorted by a given criteria (population, name, area)"
    )
    @GetMapping("/region/{region}/countries")
    public ResponseEntity<List<String>> getCountriesInRegion(
            @Parameter(
                    description = "The name of the region to retrieve countries from",
                    examples = {
                            @ExampleObject(
                                    name = "Asia",
                                    description = "Countries in Asia",
                                    value = "Asia"
                            ),
                            @ExampleObject(
                                    name = "Europe",
                                    description = "Countries in Europe",
                                    value = "Europe"
                            ),
                            @ExampleObject(
                                    name = "Africa",
                                    description = "Countries in Africa",
                                    value = "Africa"
                            )
                    },
                    required = true
            )
            @PathVariable String region,
            @Parameter(
                    description = "The criteria to sort the countries by",
                    examples = {
                            @ExampleObject(
                                    name = "Sort by name",
                                    description = "Sort countries alphabetically by name",
                                    value = "name"
                            ),
                            @ExampleObject(
                                    name = "Sort by population",
                                    description = "Sort countries by population size (descending)",
                                    value = "population"
                            ),
                            @ExampleObject(
                                    name = "Sort by area",
                                    description = "Sort countries by geographical area (descending)",
                                    value = "area"
                            )
                    }
            )
            @RequestParam(required = false) String sortBy) {
        logger.debug("Getting countries in region: {}", region);

        // Retrieve the list of countries in the specified region using CountryService
        List<Country> countries = countryService.getCountriesByRegion(region, sortBy);

        // Check if any countries were found in the region
        if (countries == null || countries.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(List.of("No countries found in the specified region"));
        }

        // Extract the common names of the countries
        List<String> countryNames = countries.stream()
                .map(country -> country.getName().getCommon())
                .collect(Collectors.toList());

        // Return the list of country names
        return ResponseEntity.ok(countryNames);
    }

    @Operation(
            summary = "Get region statistics",
            description = "Retrieves statistics about a specific region, including the total population, area, and number of countries"
    )
    @GetMapping("/region/{region}/statistics")
    public ResponseEntity<Map<String, Object>> getRegionStatistics(
            @PathVariable String region,
            @RequestParam(required = false) String format) {

        try {
            Map<String, Object> statistics = countryService.calculateRegionStatistics(region);


            // Check if "pretty" formatting is requested
            if ("pretty".equalsIgnoreCase(format)) {
                Map<String, Object> formattedStats = new HashMap<>();
                for (Map.Entry<String, Object> entry : statistics.entrySet()) {
                    Object value = entry.getValue();
                    if (value instanceof Number) {
                        // Format numbers with commas as thousands separators
                        formattedStats.put(entry.getKey(), String.format("%,d", ((Number) value).longValue()));
                    } else {
                        formattedStats.put(entry.getKey(), value);
                    }
                }
                return ResponseEntity.ok(formattedStats);
            }

            // Return the raw statistics if no formatting is requested
            return ResponseEntity.ok(statistics);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Compares two countries across various metrics including:
     * - Population ratio
     * - Area ratio
     * - Shared languages
     * - Shared borders (true if the two countries share borders)
     *
     * @param country1 First country to compare
     * @param country2 Second country to compare
     * @return Comparison data between the two countries
     */
    @Operation(
            summary = "Get country comparison",
            description = "Compares two countries across various metrics including population, area, languages, currencies, and borders"
    )
    @GetMapping("/compare/{country1}/{country2}")
    public ResponseEntity<Map<String, Object>> compareCountries(
            @PathVariable String country1,
            @PathVariable String country2) {

        logger.info("Comparing countries {} and {}", country1, country2);

        try {
            // Retrieve information about both countries using CountryService
            Country countryData1 = countryService.getCountryInfo(country1);
            Country countryData2 = countryService.getCountryInfo(country2);

            // Check if either country is not found
            if (countryData1 == null || countryData2 == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        Map.of("error", "One or both countries not found")
                );
            }

            // Initialize comparison result map
            Map<String, Object> comparisonResults = new HashMap<>();

            // Compare population
            long population1 = countryData1.getPopulation();
            long population2 = countryData2.getPopulation();
            double populationRatio = (double) population1 / population2;
            comparisonResults.put("populationRatio", populationRatio);

            // Compare area
            double area1 = countryData1.getArea();
            double area2 = countryData2.getArea();
            double areaRatio = area1 / area2;
            comparisonResults.put("areaRatio", areaRatio);

            // Compare shared languages
            Set<String> languages1 = new HashSet<>(countryData1.getLanguages().values());
            Set<String> languages2 = new HashSet<>(countryData2.getLanguages().values());
            languages1.retainAll(languages2); // Find shared languages
            comparisonResults.put("sharedLanguages", languages1);

            // Compare borders
            List<String> borders1 = countryData1.getBorders();
            List<String> borders2 = countryData2.getBorders();
            boolean directlyBordering = false;
            if (borders1 != null && borders2 != null) {
                Set<String> borderSet1 = new HashSet<>(borders1);
                borderSet1.retainAll(borders2); // Check for common borders
                directlyBordering = !borderSet1.isEmpty();
            }
            comparisonResults.put("directlyBordering", directlyBordering);

            // Return the comparison results
            return ResponseEntity.ok(comparisonResults);

        } catch (RuntimeException e) {
            // Handle exceptions and return an error response
            Map<String, Object> error = Map.of(
                    "error", "Comparison failed",
                    "message", e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
    }