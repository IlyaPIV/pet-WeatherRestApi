package rest.client.examples.locations.update;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import rest.client.examples.locations.Location;


public class UpdateLocationAsObject {

    public static void main(String[] args) {
        String requestURI = "http://localhost:8080/v1/locations";

        RestTemplate restTemplate = new RestTemplate();

        Location location = new Location();
        location.setCode("DANA_VN");
        location.setCityName("Da Nang");
        location.setRegionName("Da Nang");
        location.setCountryCode("VN");
        location.setCountryName("Vietnam");
        location.setEnabled(true);

        HttpEntity<Location> request = new HttpEntity<>(location);

        ResponseEntity<Location> response = restTemplate.exchange(requestURI, HttpMethod.PUT, request, Location.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            Location updatedLocation = response.getBody();
            System.out.println(updatedLocation);
        }
    }

}