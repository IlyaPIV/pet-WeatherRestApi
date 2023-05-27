package pet.skyapi.weatherforecast.realtime;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pet.skyapi.weatherforecast.common.Location;
import pet.skyapi.weatherforecast.common.RealtimeWeather;
import pet.skyapi.weatherforecast.geolocation.CommonIPUtility;
import pet.skyapi.weatherforecast.geolocation.GeolocationException;
import pet.skyapi.weatherforecast.geolocation.GeolocationService;
import pet.skyapi.weatherforecast.location.LocationNotFoundException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/realtime")
public class RealtimeWeatherApiController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RealtimeWeatherApiController.class);

    private final RealtimeWeatherService weatherService;
    private final GeolocationService geolocationService;
    private final ModelMapper modelMapper;

    @GetMapping
    public ResponseEntity<?> getRealtimeWeatherByIPAddress(HttpServletRequest request){
        String ipAddress = CommonIPUtility.getIPAddress(request);

        try {
            Location locationFromIP = geolocationService.getLocation(ipAddress);
            RealtimeWeather realtimeWeather = weatherService.getByLocation(locationFromIP);

            RealtimeWeatherDTO weatherDTO = modelMapper.map(realtimeWeather, RealtimeWeatherDTO.class);

            return ResponseEntity.ok(weatherDTO);
        } catch (GeolocationException ex) {
            LOGGER.error(ex.getMessage(), ex);

            return ResponseEntity.badRequest().build();
        } catch (LocationNotFoundException ex) {
            LOGGER.error(ex.getMessage(), ex);

            return ResponseEntity.notFound().build();
        }
    }


    @GetMapping("/{locationCode}")
    public ResponseEntity<?> getRealtimeWeatherByLocationCode(@PathVariable(name = "locationCode") String code){
        try {
            RealtimeWeather realtimeWeather = weatherService.getByLocationCode(code);

            return ResponseEntity.ok(entity2DTO(realtimeWeather));
        } catch (LocationNotFoundException e) {
            LOGGER.error(e.getMessage());

            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{locationCode}")
    public ResponseEntity<?> updateRealtimeWeatherByLocationCode(@PathVariable(name = "locationCode") String code,
                                                                 @RequestBody @Valid RealtimeWeather realtimeWeatherFromRequest){
        realtimeWeatherFromRequest.setLocationCode(code);

        try {
            RealtimeWeather updatedWeather = weatherService.update(code, realtimeWeatherFromRequest);

            return ResponseEntity.ok(entity2DTO(updatedWeather));
        } catch (LocationNotFoundException e) {
            LOGGER.error(e.getMessage());

            return ResponseEntity.notFound().build();
        }
    }

    private RealtimeWeatherDTO entity2DTO(RealtimeWeather realtimeWeather){
        return modelMapper.map(realtimeWeather, RealtimeWeatherDTO.class);
    }

}
