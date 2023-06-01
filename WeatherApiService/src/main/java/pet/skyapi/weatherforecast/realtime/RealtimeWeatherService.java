package pet.skyapi.weatherforecast.realtime;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pet.skyapi.weatherforecast.common.Location;
import pet.skyapi.weatherforecast.common.RealtimeWeather;
import pet.skyapi.weatherforecast.location.LocationNotFoundException;
import pet.skyapi.weatherforecast.location.LocationRepository;
import pet.skyapi.weatherforecast.location.LocationService;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class RealtimeWeatherService {

    private final RealtimeWeatherRepository realtimeWeatherRepo;
    private final LocationRepository locationRepository;

    public RealtimeWeather getByLocation(Location location) {
        String countryCode = location.getCountryCode();
        String cityName = location.getCityName();
        RealtimeWeather weather = realtimeWeatherRepo.findByCountryCodeAndCity(countryCode, cityName);

        if (weather == null){
            throw new LocationNotFoundException(countryCode, cityName);
        }

        return weather;
    }

    public RealtimeWeather getByLocationCode(String locationCode) {
        RealtimeWeather weather = realtimeWeatherRepo.findByLocationCode(locationCode);

        if (weather == null) {
            throw new LocationNotFoundException(locationCode);
        }

        return weather;
    }

    @Transactional
    public RealtimeWeather update(String locationCode, RealtimeWeather realtimeWeather) {
        Location location = locationRepository.findByCode(locationCode);
        if (location == null){
            throw new LocationNotFoundException(locationCode);
        }

        realtimeWeather.setLocation(location);
        realtimeWeather.setLastUpdate(new Date());

        if (location.getRealtimeWeather() == null) {
            location.setRealtimeWeather(realtimeWeather);
            Location updatedLocation = locationRepository.save(location);

            return updatedLocation.getRealtimeWeather();
        }

        return realtimeWeatherRepo.save(realtimeWeather);

    }
}
