package pet.skyapi.weatherforecast.location;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pet.skyapi.weatherforecast.common.Location;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LocationService {
    private final LocationRepository repository;

    public Location add(Location location){
        return repository.save(location);
    }

    public List<Location> getList(){
        return repository.findAllNotTrashed();
    }

    public Location getLocation(String code){
        return repository.findByCode(code);
    }

    public Location updateLocation(Location locationFromRequest) throws LocationNotFoundException {
        String code = locationFromRequest.getCode();

        Location locationInDB = repository.findByCode(code);

        if (locationInDB == null){
            throw new LocationNotFoundException("No location found with the given code: " + code);
        }

        locationInDB.setCityName(locationFromRequest.getCityName());
        locationInDB.setRegionName(locationFromRequest.getRegionName());
        locationInDB.setCountryCode(locationFromRequest.getCountryCode());
        locationInDB.setCountryName(locationFromRequest.getCountryName());
        locationInDB.setEnabled(locationFromRequest.isEnabled());

        return repository.save(locationInDB);
    }

    @Transactional
    public void deleteLocation(String code) throws LocationNotFoundException {
        Location location = repository.findByCode(code);

        if (location == null){
            throw new LocationNotFoundException("No location found with the given code: " + code);
        }

        repository.trashByCode(code);
    }

}
