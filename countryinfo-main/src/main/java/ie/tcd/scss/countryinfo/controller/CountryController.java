package ie.tcd.scss.countryinfo.controller;

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

        import java.util.ArrayList;
        import java.util.HashMap;
        import java.util.List;
        import java.util.Map;
        import java.util.stream.Collectors;

@RestController
@RequestMapping("/countries")
@Tag(name = "Country Information", description = "APIs for retrieving and comparing country information")
// Annotation for OpenAPI/Swagger
// See configuration in class OpenApiConfig in package config
// Access at http://localhost:8080/swagger-ui.html
public class CountryController {
    // Create logger for this class
    private static final Logger logger = LoggerFactory.getLogger(CountryController.class);

    private final CountryService countryService;

    public CountryController(CountryService countryService) {
        this.countryService = countryService;
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

        // TODO: Implement mostPopulousWithPopulation
        // Requirements:
        // - Find all countries containing the substring (case-insensitive)
        // - Sort them by population (descending)
        // - Format each country as "Name (Population)"
        // - Join results with "; " separator
        // - Handle empty results with HTTP 404
        // - Handle invalid inputs
        // Example output: "India (1380004385); Indonesia (273523615)"

        return ResponseEntity.ok("");
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

        // TODO: Implement getCountryDemographics
        // Requirements:
        // - Retrieve demographic information about the country
        // - Return a map with the following keys: population, area, languages, currencies, timezones
        // - Handle empty results with HTTP 404

        return ResponseEntity.ok(Map.of()); // Replace with actual demographic data
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

        // TODO: Implement getBorderingCountries
        // Requirements:
        // - Retrieve the countries that share a border with the specified country
        // - Return a list of country names
        // - Handle empty results with HTTP 404

        return ResponseEntity.ok(List.of()); // Replace with actual list of bordering countries
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

        // TODO: Implement getCountriesInRegion
        // Requirements:
        // - Retrieve countries in the specified region
        // - Optionally sort the countries by the given criteria (population, name, area)
        // - Handle empty results with HTTP 404

        List<Country> countries = new ArrayList<>(); // Replace with actual list of countries

        return ResponseEntity.ok(countries.stream()
                .map(c -> c.getName().getCommon())
                .collect(Collectors.toList()));
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

            // If format=pretty is specified, format the numbers for better readability

            // TODO: Implement pretty formatting
            // Requirements:
            // - If format=pretty is specified, format the numbers in the statistics map with commas as Strings with thousands separators
            // - For example, 1000000 should be formatted as "1,000,000"
            // - You need to iterate over the statistics map and format each value that is a Number
            // - Use String.format() to format the numbers with commas
            // - Return the formatted statistics map as the response

            // if ...
                // formattedStats ...
                // return ResponseEntity.ok(formattedStats);
            // ... }

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

            // TODO: Implement compareCountries


            // Requirements:
            // - Compare the two countries across various metrics
            // - Return a map with the comparison results

            return ResponseEntity.ok(Map.of()); // Replace with actual comparison data

        } catch (RuntimeException e) {
            // Create error response with details
            Map<String, Object> error = Map.of(
                    "error", "Comparison failed",
                    "message", e.getMessage()
            );
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(error);
        }
    }
}