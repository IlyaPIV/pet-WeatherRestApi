package pet.skyapi.weatherforecast.location;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import pet.skyapi.weatherforecast.common.HourlyWeather;
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
        location.setCode("WAW_PL");
        location.setCityName("Warsaw");
        //location.setRegionName("");
        location.setCountryCode("PL");
        location.setCountryName("Republic of Poland");
        location.setEnabled(true);
        location.setTrashed(false);

        Location savedLocation = repository.save(location);

        Assertions.assertNotNull(savedLocation);
        Assertions.assertEquals(savedLocation.getCode(), "WAW_PL");
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

    @Test
    public void testAddHourlyWeatherData(){
        String locationCode = "NYC_USA";
        Location location = repository.findById(locationCode).get();

        List<HourlyWeather> hourlyWeather = location.getListHourlyWeather();

        HourlyWeather forecast1 = new HourlyWeather()
                                            .id(location, 10)
                                            .temperature(15)
                                            .precipitation(40)
                                            .status("Rain");

        HourlyWeather forecast2 = new HourlyWeather()
                                            .location(location)
                                            .hourOfDay(11)
                                            .temperature(16)
                                            .precipitation(30)
                                            .status("Cloudy");

        HourlyWeather forecast3 = new HourlyWeather()
                .location(location)
                .hourOfDay(12)
                .temperature(19)
                .precipitation(25)
                .status("Sunny");

        hourlyWeather.add(forecast1);
        hourlyWeather.add(forecast2);
        hourlyWeather.add(forecast3);

        Location updatedLocation = repository.save(location);

        assertThat(updatedLocation.getListHourlyWeather()).isNotEmpty();
    }

    @Test
    public void testFindByCountryCodeAndCityNotFound(){
        String countryCode = "BZ";
        String cityName = "City";

        Location location = repository.findByCountryCodeAndCityName(countryCode, cityName);

        assertThat(location).isNull();
    }

    @Test
    public void testFindByCountryCodeAndCityIsFound(){
        String countryCode = "US";
        String cityName = "New York City";

        Location location = repository.findByCountryCodeAndCityName(countryCode, cityName);

        assertThat(location).isNotNull();
        assertThat(location.getCountryCode()).isEqualTo(countryCode);
        assertThat(location.getCityName()).isEqualTo(cityName);

        System.out.println(location);
    }

}