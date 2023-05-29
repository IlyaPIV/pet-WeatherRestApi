package pet.skyapi.weatherforecast.hourly;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pet.skyapi.weatherforecast.common.HourlyWeather;
import pet.skyapi.weatherforecast.common.Location;
import pet.skyapi.weatherforecast.location.LocationNotFoundException;
import pet.skyapi.weatherforecast.location.LocationRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HourlyWeatherService {

    private HourlyWeatherRepository hourlyRepo;
    private LocationRepository locationRepo;

    public List<HourlyWeather> getHourlyWeatherByLocation(Location incomLocation, int currentHour) throws LocationNotFoundException {
        String countryCode = incomLocation.getCountryCode();
        String cityName = incomLocation.getCityName();

        Location locationInDB = locationRepo.findByCountryCodeAndCityName(countryCode, cityName);

        if (locationInDB == null){
            throw new LocationNotFoundException("No location found with the given country code and city name");
        }

        return hourlyRepo.findByLocationCode(locationInDB.getCode(), currentHour);
    }
}
