package rest.client.examples.locations.add;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import rest.client.examples.locations.Location;

public class AddLocationAsObject {
    public static void main(String[] args) {
        String requestURI = "http://localhost:8080/v1/locations";

        RestTemplate restTemplate = new RestTemplate();

        Location location = new Location();
        location.setCode("MAD_ES");
        location.setCityName("Madrid");
        location.setRegionName("Community of Madrid");
        location.setCountryCode("ES");
        location.setCountryName("Spain");
        location.setEnabled(true);


        HttpEntity<Location> request = new HttpEntity<>(location);

        ResponseEntity<Location> response = restTemplate.postForEntity(requestURI, request, Location.class);
        HttpStatusCode statusCode = response.getStatusCode();
        System.out.println("Response status code: " + statusCode);
        Location addedLocation = response.getBody();
        System.out.println(addedLocation);

    }
}
