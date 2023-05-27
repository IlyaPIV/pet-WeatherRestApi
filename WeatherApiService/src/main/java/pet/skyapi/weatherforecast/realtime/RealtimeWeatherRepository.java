package pet.skyapi.weatherforecast.realtime;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pet.skyapi.weatherforecast.common.RealtimeWeather;

@Repository
public interface RealtimeWeatherRepository extends CrudRepository<RealtimeWeather, String> {
    @Query("SELECT r FROM RealtimeWeather r WHERE r.location.countryCode = ?1 AND r.location.cityName = ?2")
    RealtimeWeather findByCountryCodeAndCity(String countryCode, String city);

    @Query("SELECT r FROM RealtimeWeather r WHERE r.location.code = ?1 and r.location.trashed = false")
    RealtimeWeather findByLocationCode(String code);
}
