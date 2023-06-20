package rest.client.examples.locations.delete;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;

public class DeleteLocationWithCallback {

    public static void main(String[] args) {
        String requestURI = "http://localhost:8080/v1/locations/MADRID_ES";

        RestTemplate restTemplate = new RestTemplate();

        RequestCallback requestCallback = request -> {
            System.out.println("Request URI: " + request.getURI().toString());
        };

        ResponseExtractor<Void> responseExtractor = response -> {
            HttpStatusCode statusCode = response.getStatusCode();

            System.out.println("Status code: " + statusCode);

            if (statusCode.is2xxSuccessful()) {
                System.out.println("Location deleted");
            }
            return null;
        };

        restTemplate.execute(requestURI, HttpMethod.DELETE, requestCallback, responseExtractor);
    }

}