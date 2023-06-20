package rest.client.examples.locations.add;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;



public class AddLocationAsJSONString {
    public static void main(String[] args) {
        String requestURI = "http://localhost:8080/v1/locations";

        RestTemplate restTemplate = new RestTemplate();

        String json = """
                {
                    "code": "KRK_PL",
                    "city_name": "Krakow",
                    "region_name": "Malopolske",
                    "country_code": "PL",
                    "country_name": "Poland",
                    "enabled": true
                }
                """;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(json, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(requestURI, request, String.class);
        HttpStatusCode statusCode = response.getStatusCode();
        System.out.println("Response status code: " + statusCode);
        System.out.println(response.getBody());

    }
}
