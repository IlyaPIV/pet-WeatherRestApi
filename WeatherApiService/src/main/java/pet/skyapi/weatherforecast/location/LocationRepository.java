package pet.skyapi.weatherforecast.location;

import org.springframework.data.repository.CrudRepository;
import pet.skyapi.weatherforecast.common.Location;

public interface LocationRepository extends CrudRepository<Location, String> {
}
