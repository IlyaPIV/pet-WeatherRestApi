package pet.skyapi.weatherforecast.hourly;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pet.skyapi.weatherforecast.common.HourlyWeather;
import pet.skyapi.weatherforecast.common.Location;
import pet.skyapi.weatherforecast.geolocation.CommonIPUtility;
import pet.skyapi.weatherforecast.geolocation.GeolocationException;
import pet.skyapi.weatherforecast.geolocation.GeolocationService;
import pet.skyapi.weatherforecast.location.LocationNotFoundException;

import java.util.List;

@RestController
@RequestMapping("/v1/hourly")
@RequiredArgsConstructor
public class HourlyWeatherController {

    private final HourlyWeatherService hourlyWeatherService;
    private final GeolocationService geolocationService;

    @GetMapping
    public ResponseEntity<?> listHourlyForecastByIPAddress(HttpServletRequest request){
        String ipAddress = CommonIPUtility.getIPAddress(request);

        try {
            Location locationFromIP = geolocationService.getLocation(ipAddress);
            int currentHour = Integer.parseInt(request.getHeader("X-current-Hour"));

            List<HourlyWeather> hourlyWeatherForecast = hourlyWeatherService.getHourlyWeatherByLocation(locationFromIP, currentHour);
            if (hourlyWeatherForecast.isEmpty()){
                return ResponseEntity.noContent().build();
            }

            return ResponseEntity.ok(hourlyWeatherForecast);

        } catch (GeolocationException ex) {
            return ResponseEntity.badRequest().build();
        } catch (LocationNotFoundException ex) {
            return ResponseEntity.notFound().build();
        }
    }
}
