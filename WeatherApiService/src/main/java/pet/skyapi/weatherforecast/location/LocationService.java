package pet.skyapi.weatherforecast.location;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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
}
