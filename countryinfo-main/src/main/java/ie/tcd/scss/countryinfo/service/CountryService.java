package ie.tcd.scss.countryinfo.service;

import ie.tcd.scss.countryinfo.domain.Country;
import ie.tcd.scss.countryinfo.domain.Currency;
import ie.tcd.scss.countryinfo.domain.Translation;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class is responsible for access to the <a href="https://restcountries.com/">https://restcountries.com/</a> REST API to retrieve information about
 * countries. *
 */
@Service
public class CountryService {
    // Create logger for this class
    private static final Logger logger = LoggerFactory.getLogger(CountryService.class);

    private final RestTemplate restTemplate;

    // base URL to retrieve country information by name
    private static final String API_URL_BY_NAME = "https://restcountries.com/v3.1/name/";

    // base URL to retrieve country information for all countries
    private static final String API_URL_ALL = "https://restcountries.com/v3.1/all/";

    public CountryService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String getFlagForCountry(String countryName) {
        Country country = getCountryInfo(countryName);
        if (country != null && country.getFlags() != null) {
            return country.getFlags().getPng();
        }
        return null; // return null if no country found
    }

    public String getMapForCountry(String countryName) {
        Country country = getCountryInfo(countryName);
        if (country != null && country.getMaps() != null) {
            return country.getMaps().getGoogleMaps();
        }
        return null; // return null if no country found
    }

    public List<String> getContinentsForCountry(String countryName) {
        Country country = getCountryInfo(countryName);
        if (country != null && country.getContinents() != null) {
            return country.getContinents();
        }
        return List.of(); // return empty list if no countries found
    }

    public List<String> getNamesMostPopulousCountries(String substring) {
        Country[] countries = restTemplate.getForObject(API_URL_ALL, Country[].class);
        if (countries != null) {
            return Stream.of(countries) // convert to Stream

                    // TODO: in getNamesMostPopulousCountries implement filter and sort
                    .filter(country -> country.getName().getCommon().toLowerCase().contains(substring.toLowerCase())) // Filter by substring, case-insensitive
                    .sorted((c1, c2) -> Long.compare(c2.getPopulation(), c1.getPopulation())) // Sort by population, descending

                    .map(c -> c.getName().getCommon()) // map country to country name
                    .collect(Collectors.toList()); //
        }
        return List.of(); // return empty list if no countries found
    }

    public List<String> getNamesMostPopulousCountriesWithPopulation(String substring) {

        Country[] countries = restTemplate.getForObject(API_URL_ALL, Country[].class);
        if (countries == null) {
            return List.of();
        }

        // Filter countries by substring (case-insensitive) and sort by population (descending)
        return Arrays.stream(countries)
                .filter(country -> country.getName().getCommon().toLowerCase().contains(substring.toLowerCase()))
                .sorted((c1, c2) -> Long.compare(c2.getPopulation(), c1.getPopulation())) // Sort descending by population
                .map(country -> String.format("%s (%d)", country.getName().getCommon(), country.getPopulation())) // Map to "name (population)"
                .collect(Collectors.toList()); // Collect the mapped strings into a list
    }

    /**
     * Retrieves information about a country matching the given name. If multiple countries are found, only the first
     * one is returned.
     *
     * @param countryName The name of the country or countries to retrieve.
     * @return A Country object with the information about the found country, or null if no country found
     */
    public Country getCountryInfo(String countryName) {
        try {
            Country[] countries = restTemplate.getForObject(API_URL_BY_NAME + countryName, Country[].class);
            return countries != null && countries.length > 0 ? countries[0] : null; // return first country if found
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                // Country not found
                return null;
            }
            // If it's another kind of error, rethrow it
            throw e;
        }
    }

     /**
     * Retrieves information about all countries matching the given name.
     *
     * @param countryName The name of the country or countries to retrieve.
     * @return A List of Country objects.
     */
    public List<Country> getCountriesInfo(String countryName) {
        try {
            Country[] countries = restTemplate.getForObject(API_URL_BY_NAME + countryName, Country[].class);
            // If countries are found, convert the array to a List and return it
            if (countries != null) {
                return Arrays.asList(countries);
            }
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                // No countries found with that name, return an empty list
                return List.of();
            }
            // If it's another kind of error, rethrow it
            throw e;
        }
        // Return an empty list if no countries were found
        return List.of();
    }


    /**
     * Retrieves the translation for the country name in the specified language.
     *
     * @param countryName The name of the country.
     * @param language The language code for the translation (e.g., 'ger' for German).
     * @return The translated name of the country if available, otherwise null.
     */
    public String getTranslationForCountry(String countryName, String language) {
        Country country = getCountryInfo(countryName);

        if (country == null) {
            return null;
        }

        Map<String, Translation> translations = country.getTranslations();
        if (translations == null || !translations.containsKey(language)) {
            return null;
        }

        Translation translation = translations.get(language);
        return translation != null ? translation.getCommon() : null;
    }

    public List<Country> getCountriesByRegion(String region, String sortBy) {
        logger.debug("Retrieving countries for region: {}", region);

        Country[] countries = restTemplate.getForObject(API_URL_ALL, Country[].class);
        if (countries == null) {
            return List.of();
        }

        Stream<Country> countryStream = Arrays.stream(countries)
                .filter(country -> region.equalsIgnoreCase(country.getRegion()));

        if ("population".equalsIgnoreCase(sortBy)) {
            countryStream = countryStream.sorted((c1, c2) -> Long.compare(c2.getPopulation(), c1.getPopulation())); // Sort by population, descending
        } else if ("name".equalsIgnoreCase(sortBy)) {
            countryStream = countryStream.sorted(Comparator.comparing(c -> c.getName().getCommon(), String.CASE_INSENSITIVE_ORDER)); // Sort by name, ascending
        } else if ("area".equalsIgnoreCase(sortBy)) {
            countryStream = countryStream.sorted((c1, c2) -> Double.compare(c2.getArea(), c1.getArea())); // Sort by area, descending
        }

        // Collect and return the sorted (or unsorted) list of countries
        return countryStream.collect(Collectors.toList());
    }
    /**
     * Calculates statistics for a given region, including the total population, total area, average population density,
     * and the most common languages and currencies.
     *
     * @param region The region for which to calculate statistics.
     * @return A map containing the calculated statistics, in particular
     * - totalPopulation: the total population of all countries in the region
     * - totalArea: the total area of all countries in the region
     * - averagePopulationDensity: the average population density of all countries in the region
     * - mostCommonLanguages: the most common languages spoken in the region, with the number of countries speaking each language, up to 5 languages
     *      Each entry in the map is a language code (e.g., "eng") mapped to the number of countries speaking that language. So for instance,
     *      if the map contains the entries "eng" -> 3, "fra" -> 2, it means that English is spoken in 3 countries and French in 2 countries.
     * - mostCommonCurrencies: the most common currencies used in the region, with the number of countries using each currency, up to 5 currencies
     *      Each entry in the map is a currency code (e.g., "USD", "EUR") mapped to the number of countries using that currency. So for instance,
     *      if the map contains the entries "USD" -> 4, "EUR" -> 3, it means that the US Dollar is used in 4 countries and the Euro in 3 countries.
     *
     *  One example result for the region "Europe" could be (with dummy values)
     *  {
     *    "totalPopulation": 740000000,
     *    "totalArea": 23000000,
     *    "averagePopulationDensity": 30,
     *    "mostCommonLanguages": {
     *      "eng": 9,
     *      "fra": 8,
     *      "deu": 7,
     *      "rus": 6,
     *      "ita": 5
     *    },
     *    "mostCommonCurrencies": {
     *      "EUR": 5,
     *      "RUB": 4,
     *      "GBP": 3,
     *      "CHF": 2,
     *      "SEK": 1
     *    }
     *    }
     */
    public Map<String, Object> calculateRegionStatistics(String region) {
        List<Country> countries = getCountriesByRegion(region, null);
        logger.debug("Calculating statistics for region: {}", region);

        Map<String, Object> result = new HashMap<>();
        double totalPopulation = 0.0;
        double totalArea = 0.0;

        // Calculate total population and total area
        for (Country country : countries) {
            totalPopulation += country.getPopulation();
            totalArea += country.getArea();
        }

        double averagePopulationDensity = totalPopulation / totalArea;
        result.put("totalPopulation", totalPopulation);
        result.put("totalArea", totalArea);
        result.put("averagePopulationDensity", averagePopulationDensity);

        // Calculate frequency of languages
        Map<String, Long> languageFrequency = new HashMap<>();
        for (Country country : countries) {
            Map<String, String> languages = country.getLanguages();
            if (languages != null) {
                for (String language : languages.values()) {
                    languageFrequency.put(language, languageFrequency.getOrDefault(language, 0L) + 1);
                }
            }
        }

        // Get top 5 most common languages using the getTopN method
        Map<String, Long> topLanguages = getTopN(languageFrequency, 5);
        result.put("mostCommonLanguages", topLanguages);

        // Calculate frequency of currencies
        Map<String, Long> currencyFrequency = new HashMap<>();
        for (Country country : countries) {
            Map<String, Currency> currencies = country.getCurrencies();
            if (currencies != null) {
                for (String currencyCode : currencies.keySet()) {
                    currencyFrequency.put(currencyCode, currencyFrequency.getOrDefault(currencyCode, 0L) + 1);
                }
            }
        }

        // Get top 5 most common currencies using the getTopN method
        Map<String, Long> topCurrencies = getTopN(currencyFrequency, 5);
        result.put("mostCommonCurrencies", topCurrencies);

        return result;
    }



    /**
     * Compares two countries based on population, area, languages, and borders.
     * @param  country1Name name of the first country
     * @param country2Name name of the second country
     * @return A map containing the comparison results, in particular it has the following keys and corresponding values
     * - populationRatio: the ratio of the population of country 1 to the population of country 2
     * - areaRatio: the ratio of the area of country 1 to the area of country 2
     * - sharedLanguages: the set of languages spoken in both countries (as List<String> of Strings like "English", "French", i.e., the value side of the languages map in the Country object "fra" -> "French")
     * - directlyBordering: a Boolean indicating whether the two countries share a border
     *
     * Note that country.getBorders() returns null if the country is an island for has no land borders.
     */
    public Map<String, Object> compareCountries(String country1Name, String country2Name) {
        logger.debug("Comparing countries: {} and {}", country1Name, country2Name);

        // Initialize the map to store the results
        Map<String, Object> comparisonResults = new HashMap<>();

        // Retrieve population of both countries and calculate the population ratio
        double population1 = (double) getCountryInfo(country1Name).getPopulation();
        double population2 = (double) getCountryInfo(country2Name).getPopulation();
        double populationRatio = population1 / population2;
        comparisonResults.put("populationRatio", populationRatio);

        // Retrieve area of both countries and calculate the area ratio
        double area1 = (double) getCountryInfo(country1Name).getArea();
        double area2 = (double) getCountryInfo(country2Name).getArea();
        double areaRatio = area1 / area2;
        comparisonResults.put("areaRatio", areaRatio);

        // Retrieve and compare languages
        Map<String, String> lan1 = getCountryInfo(country1Name).getLanguages();
        Map<String, String> lan2 = getCountryInfo(country2Name).getLanguages();
        List<String> sharedLanguages = new ArrayList<>();
        if (lan1 != null && lan2 != null) {
            Set<String> languagesSet1 = new HashSet<>(lan1.values());
            Set<String> languagesSet2 = new HashSet<>(lan2.values());
            languagesSet1.retainAll(languagesSet2); // Retain only common languages
            sharedLanguages.addAll(languagesSet1);
        }
        comparisonResults.put("sharedLanguages", sharedLanguages);

        // Retrieve and compare borders to determine if countries share a border
        List<String> borders1 = getCountryInfo(country1Name).getBorders();
        List<String> borders2 = getCountryInfo(country2Name).getBorders();
        boolean directlyBordering = false;
        if (borders1 != null && borders2 != null) {
            Set<String> borderSet1 = new HashSet<>(borders1);
            borderSet1.retainAll(borders2); // Check for any common borders
            directlyBordering = !borderSet1.isEmpty();
        }
        comparisonResults.put("directlyBordering", directlyBordering);

        // Return the comparison results
        return comparisonResults;
    }
        // - Calculate population ratio (population of country1 / population of country2)
        // - Calculate area ratio (area of country1 / area of country2)
        // - Find shared languages between the countries
        // - Determine if countries share a border
        // - Handle null cases (e.g., island nations with no borders)
        // Return results in a Map with keys: "populationRatio", "areaRatio",
        // "sharedLanguages", "directlyBordering"



    /**
     * Returns the top N entries from a map, sorted by value in descending order.
     * @param map   The map to get the top N entries from.
     * @param n     The number of entries to return.
     * @return A    LinkedHashMap containing the top N entries from the input map.
     * @param <K>   The type of the keys in the map.
     */
    private <K> Map<K, Long> getTopN(Map<K, Long> map, int n) {
        logger.debug("Getting top {} entries from map: {}", n, map);

        LinkedHashMap<K, Long> result = map.entrySet().stream()
                    .sorted(Map.Entry.<K, Long>comparingByValue().reversed())
                    .limit(n)
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue,
                            (e1, e2) -> e1,
                            LinkedHashMap::new
                    ));
        logger.debug("Top {} entries: {}", n, result);

        return result;
    }

}
