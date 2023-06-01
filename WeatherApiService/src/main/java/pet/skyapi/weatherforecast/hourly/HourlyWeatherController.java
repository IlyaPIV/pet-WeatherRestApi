package pet.skyapi.weatherforecast.hourly;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pet.skyapi.weatherforecast.BadRequestException;
import pet.skyapi.weatherforecast.common.HourlyWeather;
import pet.skyapi.weatherforecast.common.Location;
import pet.skyapi.weatherforecast.geolocation.CommonIPUtility;
import pet.skyapi.weatherforecast.geolocation.GeolocationException;
import pet.skyapi.weatherforecast.geolocation.GeolocationService;
import pet.skyapi.weatherforecast.location.LocationNotFoundException;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/v1/hourly")
@Validated
@RequiredArgsConstructor
public class HourlyWeatherController {

    private final HourlyWeatherService hourlyWeatherService;
    private final GeolocationService geolocationService;
    private final ModelMapper modelMapper;

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

            return ResponseEntity.ok(listEntity2DTO(hourlyWeatherForecast));

        } catch (NumberFormatException ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{locationCode}")
    public ResponseEntity<?> listHourlyForecastByLocationCode(@PathVariable(name = "locationCode") String locationCode,
                                                               HttpServletRequest request){

        try {
            int currentHour = Integer.parseInt(request.getHeader("X-current-Hour"));

            List<HourlyWeather> hourlyWeatherForecast = hourlyWeatherService.getHourlyWeatherByLocationCode(locationCode, currentHour);
            if(hourlyWeatherForecast.isEmpty()){
                return ResponseEntity.noContent().build();
            }

            return ResponseEntity.ok(listEntity2DTO(hourlyWeatherForecast));

        } catch (NumberFormatException ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{locationCode}")
    public ResponseEntity<?> updateHourlyForecast(@PathVariable(name = "locationCode") String locationCode,
                                                  @RequestBody @Valid List<HourlyWeatherDTO> listDto) throws BadRequestException {
        if (listDto.isEmpty()){
            throw new BadRequestException("Hourly forecast data cannot be empty");
        }

        List<HourlyWeather> listHourlyWeather = listDTO2Entity(listDto);

        List<HourlyWeather> updatedForecast = hourlyWeatherService.updateByLocationCode(locationCode, listHourlyWeather);

        return ResponseEntity.ok(listEntity2DTO(updatedForecast));

    }

    private HourlyWeatherListDTO listEntity2DTO(List<HourlyWeather> hourlyForecast){
        Location location = hourlyForecast.get(0).getId().getLocation();

        HourlyWeatherListDTO resultDTO = new HourlyWeatherListDTO();
        resultDTO.setLocation(location.toString());

        hourlyForecast.forEach(forecast ->{
            resultDTO.addWeatherHourlyDTO(modelMapper.map(forecast, HourlyWeatherDTO.class));
        });

        return resultDTO;
    }

    private List<HourlyWeather> listDTO2Entity(List<HourlyWeatherDTO> listDTO) {
        List<HourlyWeather> listEntity = new ArrayList<>();

        listDTO.forEach(dto -> {
            listEntity.add(modelMapper.map(dto, HourlyWeather.class));
        });

        return listEntity;
    }

}
