package pet.skyapi.weatherforecast.location;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pet.skyapi.weatherforecast.common.Location;

import java.net.URI;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/locations")
public class LocationApiController {

    private final LocationService service;

    @PostMapping
    public ResponseEntity<Location> addLocation(@RequestBody @Valid Location location){
        Location added = service.add(location);
        URI uri = URI.create("/v1/locations/" + added.getCode());

        return ResponseEntity.created(uri).body(added);
    }
}
