package rest.client.examples.locations.list;


import org.springframework.web.client.*;
import rest.client.examples.locations.Location;


public class ListLocationsAsObjectArray {
    public static void main(String[] args) {
        String requestURI = "http://localhost:8080/v1/locations";

        RestTemplate restTemplate = new RestTemplate();

        try {
            Location[] response = restTemplate.getForObject(requestURI, Location[].class);
            if (response != null & response.length > 0) {
                for (Location location : response) {
                    System.out.println(location);
                }
            }
        } catch (HttpClientErrorException ex) {
            System.out.println("Client Error: " + ex.getStatusCode() + " - " + ex.getStatusText());
            ex.printStackTrace();
        } catch (HttpServerErrorException ex) {
            System.out.println("Server Error: " + ex.getStatusCode() + " - " + ex.getStatusText());
            ex.printStackTrace();
        } catch (UnknownHttpStatusCodeException ex) {
            System.out.println("Unknown HTTP status code error: " + ex.getStatusCode());
            ex.printStackTrace();
        }
    }
}
