package com.example.tutoria1.APIs.GoogleMaps;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class Geocoder {

	private static final String GEOCODING_RESOURCE = "https://maps.googleapis.com/maps/api/place/findplacefromtext/json?fields=all&inputtype=textquery&key=";
    private final String apiKey;
    public Geocoder(@Value("${google.maps.api-key}") String apiKey) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException(
                    "ERROR: No se detecta GOOGLE_MAPS_API_KEY en el archivo .env"
            );
        }
        this.apiKey = apiKey;
    }
    
    public String GeocodeSync(String query) throws IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();
        String encodedQuery = URLEncoder.encode(query,"UTF-8");
        String requestUri = GEOCODING_RESOURCE + apiKey + "&input=" + encodedQuery;
        HttpRequest geocodingRequest = HttpRequest.newBuilder().GET().uri(URI.create(requestUri))
                .timeout(Duration.ofMillis(2000)).build();
        HttpResponse<?> geocodingResponse = httpClient.send(geocodingRequest,
                HttpResponse.BodyHandlers.ofString());
        return (String) geocodingResponse.body();
    }
    
    public String getLatLng(String query) throws IOException, InterruptedException {
    	ObjectMapper mapper = new ObjectMapper();
        String response = this.GeocodeSync(query);
        JsonNode responseJsonNode = mapper.readTree(response);
        JsonNode items = responseJsonNode.get("candidates");
        String latitud = items.get(0).get("geometry").get("location").get("lat").asText(); 
        String longitud = items.get(0).get("geometry").get("location").get("lng").asText();
        return latitud + "," + longitud;
    }
}