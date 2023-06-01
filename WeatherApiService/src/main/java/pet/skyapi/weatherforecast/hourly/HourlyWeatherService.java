package pet.skyapi.weatherforecast.hourly;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pet.skyapi.weatherforecast.common.HourlyWeather;
import pet.skyapi.weatherforecast.common.Location;
import pet.skyapi.weatherforecast.location.LocationNotFoundException;
import pet.skyapi.weatherforecast.location.LocationRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HourlyWeatherService {

    private final HourlyWeatherRepository hourlyRepo;
    private final LocationRepository locationRepo;

    public List<HourlyWeather> getHourlyWeatherByLocation(Location incomLocation, int currentHour) {
        String countryCode = incomLocation.getCountryCode();
        String cityName = incomLocation.getCityName();

        Location locationInDB = locationRepo.findByCountryCodeAndCityName(countryCode, cityName);

        if (locationInDB == null){
            throw new LocationNotFoundException(countryCode, cityName);
        }

        return hourlyRepo.findByLocationCode(locationInDB.getCode(), currentHour);
    }

    public List<HourlyWeather> getHourlyWeatherByLocationCode(String locationCode, int currentHour) {
        Location locationInDB = locationRepo.findByCode(locationCode);

        if (locationInDB == null) {
            throw new LocationNotFoundException(locationCode);
        }

        return hourlyRepo.findByLocationCode(locationCode, currentHour);
    }

    public List<HourlyWeather> updateByLocationCode(String locationCode, List<HourlyWeather> hourlyForecastFromRequest) {
        Location location = locationRepo.findByCode(locationCode);
        if (location == null){
            throw new LocationNotFoundException(locationCode);
        }

        hourlyForecastFromRequest.forEach(forecast ->{
            forecast.getId().setLocation(location);
        });

        List<HourlyWeather> hourlyWeatherInDB = location.getListHourlyWeather();
        List<HourlyWeather> hourlyWeatherToBeRemoved = new ArrayList<>();
        for (HourlyWeather item:
                hourlyWeatherInDB) {
            if (!hourlyForecastFromRequest.contains(item)){
                hourlyWeatherToBeRemoved.add(item.getShallowCopy());
            }
        }
        for (HourlyWeather item : hourlyWeatherToBeRemoved){
            hourlyWeatherInDB.remove(item);
        }


        return (List<HourlyWeather>) hourlyRepo.saveAll(hourlyForecastFromRequest);
    }
}
