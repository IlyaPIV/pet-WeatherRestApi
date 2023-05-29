package pet.skyapi.weatherforecast.hourly;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import pet.skyapi.weatherforecast.common.HourlyWeather;
import pet.skyapi.weatherforecast.common.HourlyWeatherId;
import pet.skyapi.weatherforecast.common.Location;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
class HourlyWeatherRepositoryTest {

    @Autowired private HourlyWeatherRepository repository;


    @Test
    public void testAdd(){
        String locationCode = "DELHI_IN";
        int hourOfDay = 12;

        Location location = new Location();
        location.setCode(locationCode);

        HourlyWeather forecast = new HourlyWeather()
                .location(location)
                .hourOfDay(hourOfDay)
                .temperature(-5)
                .precipitation(40)
                .status("Snow");

        HourlyWeather updatedForecast = repository.save(forecast);

        assertThat(updatedForecast.getId().getLocation().getCode()).isEqualTo(locationCode);
        assertThat(updatedForecast.getId().getHourOfDay()).isEqualTo(hourOfDay);
    }

    @Test
    public void testDelete(){
        String locationCode = "DELHI_IN";
        int hourOfDay = 12;

        Location location = new Location();
        location.setCode(locationCode);

        HourlyWeatherId id = new HourlyWeatherId(hourOfDay, location);

        repository.deleteById(id);

        Optional<HourlyWeather> resultDeleting = repository.findById(id);

        assertThat(resultDeleting).isNotPresent();
    }

    @Test
    public void testFindByLocationCodeFound(){
        String locationCode = "NYC_USA";
        int currentHour = 10;

        List<HourlyWeather> hourlyForecast = repository.findByLocationCode(locationCode, currentHour);

        assertThat(hourlyForecast).isNotEmpty();
    }

    @Test
    public void testFindByLocationCodeNotFound(){
        String locationCode = "NYC-USA";
        int currentHour = 15;

        List<HourlyWeather> hourlyForecast = repository.findByLocationCode(locationCode, currentHour);

        assertThat(hourlyForecast).isEmpty();
    }
}