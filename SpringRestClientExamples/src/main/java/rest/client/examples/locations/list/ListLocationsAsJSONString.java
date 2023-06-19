package rest.client.examples.locations.list;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

public class ListLocationsAsJSONString {
    public static void main(String[] args) {
        String requestURI = "http://localhost:8080/v1/locations";

        RestTemplate restTemplate = new RestTemplate();

        try {
            String response = restTemplate.getForObject(requestURI, String.class);
            System.out.println(response);
        } catch (RestClientResponseException ex) {
            HttpStatusCode statusCode = ex.getStatusCode();
            System.out.println("HTTP status code: " + statusCode);
            ex.printStackTrace();
        }
    }
}
