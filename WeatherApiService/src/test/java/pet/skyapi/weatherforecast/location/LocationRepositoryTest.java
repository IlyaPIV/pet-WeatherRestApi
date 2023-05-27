package pet.skyapi.weatherforecast.location;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import pet.skyapi.weatherforecast.common.Location;
import pet.skyapi.weatherforecast.common.RealtimeWeather;

import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
class LocationRepositoryTest {
    @Autowired
    private LocationRepository repository;

    @Test
    public void testAddSuccess(){
        Location location = new Location();
        location.setCode("NYC_USA");
        location.setCityName("New York City");
        location.setRegionName("New York");
        location.setCountryCode("US");
        location.setCountryName("United States of America");
        location.setEnabled(true);
        location.setTrashed(false);

        Location savedLocation = repository.save(location);

        Assertions.assertNotNull(savedLocation);
        Assertions.assertEquals(savedLocation.getCode(), "NYC_USA");
    }

    @Test
    void testListSuccess(){
        List<Location> locationList = repository.findAllNotTrashed();

        locationList.forEach(System.out::println);

        assertThat(locationList.size()).isGreaterThan(0);
    }

    @Test
    void testGetNotFound(){
        String code = "ABCD";
        Location location = repository.findByCode(code);

        Assertions.assertNull(location);
    }

    @Test
    void testGetSuccess(){
        String code = "NYC_USA";
        Location location = repository.findByCode(code);

        System.out.println(location);

        assertThat(location).isNotNull();
        Assertions.assertEquals(location.getCode(), code);
    }

    @Test
    void testTrashSuccess(){
        String code = "NYC_USA";

        repository.trashByCode(code);

        Location location = repository.findByCode(code);

        Assertions.assertNull(location);
    }

    @Test
    public void testAddRealtimeWeatherData(){
        String code = "NYC_USA";

        Location location = repository.findByCode(code);

        RealtimeWeather realtimeWeather = location.getRealtimeWeather();

        if (realtimeWeather == null){
            realtimeWeather = new RealtimeWeather();
            realtimeWeather.setLocation(location);
            location.setRealtimeWeather(realtimeWeather);
        }

        realtimeWeather.setTemperature(-1);
        realtimeWeather.setHumidity(30);
        realtimeWeather.setPrecipitation(43);
        realtimeWeather.setStatus("Snowy");
        realtimeWeather.setWindSpeed(15);
        realtimeWeather.setLastUpdate(new Date());

        Location updatedLocation = repository.save(location);

        assertThat(updatedLocation.getRealtimeWeather().getLocationCode()).isEqualTo(code);
    }

}