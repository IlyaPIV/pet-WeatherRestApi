package pet.skyapi.weatherforecast.location;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import pet.skyapi.weatherforecast.common.Location;

import java.util.List;

public interface LocationRepository extends CrudRepository<Location, String> {
    @Query("select l from Location l where l.trashed = false")
    public List<Location> findAllNotTrashed();

    @Query("SELECT l FROM Location l WHERE l.trashed = false and l.code LIKE ?1")
    public Location findByCode(String code);

    @Modifying
    @Query("UPDATE Location l SET l.trashed = true WHERE l.code = ?1")
    public void trashByCode(String code);

    @Query("SELECT l FROM Location l WHERE l.countryCode = ?1 AND l.cityName = ?2 and l.trashed = false")
    public Location findByCountryCodeAndCityName(String countryCode, String cityName);
}
