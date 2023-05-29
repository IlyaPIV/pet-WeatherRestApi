package pet.skyapi.weatherforecast.hourly;


import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pet.skyapi.weatherforecast.common.HourlyWeather;
import pet.skyapi.weatherforecast.common.HourlyWeatherId;

import java.util.List;

@Repository
public interface HourlyWeatherRepository extends CrudRepository<HourlyWeather, HourlyWeatherId> {

    @Query("""
            SELECT h FROM HourlyWeather h
            WHERE h.id.location.code = ?1
                    AND h.id.hourOfDay > ?2
                    AND h.id.location.trashed = false
            """)
    public List<HourlyWeather> findByLocationCode(String locationCode, int currentHour);
}
