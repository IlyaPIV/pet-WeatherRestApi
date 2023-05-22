package pet.skyapi.weatherforecast.location;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pet.skyapi.weatherforecast.common.Location;

import java.net.URI;
import java.util.List;

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

    @GetMapping
    public ResponseEntity<?> getListLocations(){
        List<Location> locationList = service.getList();
        if(locationList.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(locationList);
        }
    }

    @GetMapping("/{code}")
    public ResponseEntity<?> getLocationByCode(@PathVariable(name = "code") String code){
        Location location = service.getLocation(code);

        if (location == null){
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(location);
    }
}
