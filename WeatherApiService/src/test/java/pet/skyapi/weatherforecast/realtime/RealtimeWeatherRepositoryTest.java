package pet.skyapi.weatherforecast.realtime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import pet.skyapi.weatherforecast.common.RealtimeWeather;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Rollback(value = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class RealtimeWeatherRepositoryTest {

    @Autowired
    private RealtimeWeatherRepository repository;

    @Test
    public void testUpdate(){
        String code = "NYC_USA";

        RealtimeWeather realtimeWeather = repository.findById(code).get();

        realtimeWeather.setTemperature(-2);
        realtimeWeather.setHumidity(35);
        realtimeWeather.setPrecipitation(47);
        realtimeWeather.setStatus("Snowy");
        realtimeWeather.setWindSpeed(25);
        realtimeWeather.setLastUpdate(new Date());

        RealtimeWeather updatedWeather = repository.save(realtimeWeather);

        assertThat(updatedWeather.getHumidity()).isEqualTo(35);

    }

    @Test
    public void testFindByCountryCodeAndCityNotFound(){
        String countryCode = "JP";
        String cityName = "Tokio";

        RealtimeWeather weather = repository.findByCountryCodeAndCity(countryCode, cityName);

        assertThat(weather).isNull();
    }

    @Test
    public void testFindByCountryCodeAndCityIsFound(){
        String countryCode = "US";
        String cityName = "New York City";

        RealtimeWeather weather = repository.findByCountryCodeAndCity(countryCode, cityName);

        assertThat(weather).isNotNull();
        assertThat(weather.getLocation().getCityName()).isEqualTo(cityName);

        System.out.println(weather);
    }

    @Test
    public void testFindByLocationCodeNotFound(){
        String locationCode = "BR_BY";

        RealtimeWeather weather = repository.findByLocationCode(locationCode);

        assertThat(weather).isNull();
    }


    @Test
    public void testFindByLocationCodeIsFound(){
        String locationCode = "NYC_USA";

        RealtimeWeather weather = repository.findByLocationCode(locationCode);

        assertThat(weather).isNotNull();
        assertThat(weather.getLocation().getCode()).isEqualTo(locationCode);

        System.out.println(weather);
    }

}