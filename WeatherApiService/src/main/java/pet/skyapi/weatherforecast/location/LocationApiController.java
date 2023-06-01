package pet.skyapi.weatherforecast.location;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pet.skyapi.weatherforecast.common.Location;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/locations")
public class LocationApiController {

    private final LocationService service;
    private final ModelMapper modelMapper;

    @PostMapping
    public ResponseEntity<LocationDTO> addLocation(@RequestBody @Valid LocationDTO dto){
        Location addedLocation = service.add(dto2Entity(dto));
        URI uri = URI.create("/v1/locations/" + addedLocation.getCode());

        return ResponseEntity.created(uri).body(entity2Dto(addedLocation));
    }

    @GetMapping
    public ResponseEntity<?> getListLocations(){
        List<Location> locationList = service.getList();
        if(locationList.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(listEntity2Dto(locationList));
        }
    }

    @GetMapping("/{code}")
    public ResponseEntity<?> getLocationByCode(@PathVariable(name = "code") String code) {
        Location location = service.getLocation(code);

        return ResponseEntity.ok(entity2Dto(location));
    }

    @PutMapping
    public ResponseEntity<?> updateLocation(@RequestBody @Valid LocationDTO dto) {
       Location updatedLocation = service.updateLocation(dto2Entity(dto));

       return ResponseEntity.ok(entity2Dto(updatedLocation));
    }

    @DeleteMapping("/{code}")
    public ResponseEntity<?> deleteLocation(@PathVariable(name = "code") String code) {
        service.deleteLocation(code);

        return ResponseEntity.noContent().build();
    }

    private LocationDTO entity2Dto(Location entity){
        return modelMapper.map(entity, LocationDTO.class);
    }

    private Location dto2Entity(LocationDTO dto){
        return modelMapper.map(dto, Location.class);
    }

    private List<LocationDTO> listEntity2Dto(List<Location> listEntity){
        return listEntity.stream().map(this::entity2Dto)
                                    .collect(Collectors.toList());
    }

}
