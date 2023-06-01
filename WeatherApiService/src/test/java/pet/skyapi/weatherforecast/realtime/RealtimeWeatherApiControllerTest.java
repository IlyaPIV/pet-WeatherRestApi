package pet.skyapi.weatherforecast.realtime;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import pet.skyapi.weatherforecast.common.Location;
import pet.skyapi.weatherforecast.common.RealtimeWeather;
import pet.skyapi.weatherforecast.geolocation.GeolocationException;
import pet.skyapi.weatherforecast.geolocation.GeolocationService;
import pet.skyapi.weatherforecast.location.LocationNotFoundException;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RealtimeWeatherApiController.class)
class RealtimeWeatherApiControllerTest {

    private static final String END_POINT_PATH = "/v1/realtime";
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper mapper;
    @MockBean
    RealtimeWeatherService weatherService;
    @MockBean
    GeolocationService geolocationService;

    @Test
    public void testGetShouldReturnStatus400BadRequest() throws Exception {
        Mockito.when(geolocationService.getLocation(Mockito.anyString())).thenThrow(GeolocationException.class);

        mockMvc.perform(get(END_POINT_PATH))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    public void testGetShouldReturnStatus404NotFound() throws Exception {
        Location location = new Location();

        Mockito.when(geolocationService.getLocation(Mockito.anyString())).thenReturn(location);
        Mockito.when(weatherService.getByLocation(location)).thenThrow(LocationNotFoundException.class);

        mockMvc.perform(get(END_POINT_PATH))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    public void testGetShouldReturnStatus200Ok() throws Exception {
        Location location = new Location();
        location.setCode("SFCA_USA");
        location.setCityName("San Francisco");
        location.setRegionName("California");
        location.setCountryName("United States of America");
        location.setCountryCode("US");

        RealtimeWeather realtimeWeather = new RealtimeWeather();
        realtimeWeather.setTemperature(-2);
        realtimeWeather.setHumidity(55);
        realtimeWeather.setPrecipitation(67);
        realtimeWeather.setStatus("Snowy");
        realtimeWeather.setWindSpeed(5);
        realtimeWeather.setLastUpdate(new Date());

        realtimeWeather.setLocation(location);
        location.setRealtimeWeather(realtimeWeather);

        Mockito.when(geolocationService.getLocation(Mockito.anyString())).thenReturn(location);
        Mockito.when(weatherService.getByLocation(location)).thenReturn(realtimeWeather);

        String expectedLocation = location.getCityName() + ", " + location.getRegionName() + ", " + location.getCountryName();


        mockMvc.perform(get(END_POINT_PATH))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.location", is(expectedLocation)))
                .andDo(print());
    }

    @Test
    public void testGetByLocationCodeShouldReturnStatus404NotFound() throws Exception{
        String locationCode = "BR_BY";

        Mockito.when(weatherService.getByLocationCode(locationCode)).thenThrow(LocationNotFoundException.class);

        String requestURI = END_POINT_PATH + "/" + locationCode;

        mockMvc.perform(get(requestURI))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    public void testGetByLocationCodeShouldReturnStatus200Ok() throws Exception {
        String locationCode = "SFCA_USA";

        Location location = new Location();
        location.setCode(locationCode);
        location.setCityName("San Francisco");
        location.setRegionName("California");
        location.setCountryName("United States of America");
        location.setCountryCode("US");

        RealtimeWeather realtimeWeather = new RealtimeWeather();
        realtimeWeather.setTemperature(-2);
        realtimeWeather.setHumidity(55);
        realtimeWeather.setPrecipitation(67);
        realtimeWeather.setStatus("Snowy");
        realtimeWeather.setWindSpeed(5);
        realtimeWeather.setLastUpdate(new Date());

        realtimeWeather.setLocation(location);
        location.setRealtimeWeather(realtimeWeather);

        Mockito.when(weatherService.getByLocationCode(locationCode)).thenReturn(realtimeWeather);

        String expectedLocation = location.getCityName() + ", " + location.getRegionName() + ", " + location.getCountryName();

        String requestURI = END_POINT_PATH + "/" + locationCode;

        mockMvc.perform(get(requestURI))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.location", is(expectedLocation)))
                .andDo(print());
    }

    @Test
    public void testUpdateShouldReturn400BadRequest() throws Exception{
        String locationCode = "ABC_DE";
        String requestURI = END_POINT_PATH + "/" + locationCode;

        RealtimeWeather realtimeWeather = new RealtimeWeather();
        realtimeWeather.setTemperature(-52);
        realtimeWeather.setHumidity(105);
        realtimeWeather.setPrecipitation(-7);
        realtimeWeather.setStatus("Ok");
        realtimeWeather.setWindSpeed(-2);

        String bodyContent = mapper.writeValueAsString(realtimeWeather);

        mockMvc.perform(put(requestURI).contentType("application/json").content(bodyContent))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    public void testUpdateShouldReturn404NotFound() throws Exception{
        String locationCode = "ABC_DE";
        String requestURI = END_POINT_PATH + "/" + locationCode;

        RealtimeWeather realtimeWeather = new RealtimeWeather();
        realtimeWeather.setTemperature(-2);
        realtimeWeather.setHumidity(55);
        realtimeWeather.setPrecipitation(67);
        realtimeWeather.setStatus("Snowy");
        realtimeWeather.setWindSpeed(5);
        realtimeWeather.setLocationCode(locationCode);

        String bodyContent = mapper.writeValueAsString(realtimeWeather);

        LocationNotFoundException ex = new LocationNotFoundException(locationCode);

        Mockito.when(weatherService.update(locationCode, realtimeWeather)).thenThrow(ex);

        mockMvc.perform(put(requestURI).contentType("application/json").content(bodyContent))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    public void testUpdateShouldReturn200Ok() throws Exception{
        String locationCode = "SFCA_US";
        String requestURI = END_POINT_PATH + "/" + locationCode;

        RealtimeWeather realtimeWeather = new RealtimeWeather();
        realtimeWeather.setTemperature(-2);
        realtimeWeather.setHumidity(55);
        realtimeWeather.setPrecipitation(67);
        realtimeWeather.setStatus("Snowy");
        realtimeWeather.setWindSpeed(5);
        realtimeWeather.setLastUpdate(new Date());

        Location location = new Location();
        location.setCode(locationCode);
        location.setCityName("San Francisco");
        location.setRegionName("California");
        location.setCountryName("United States of America");
        location.setCountryCode("US");

        realtimeWeather.setLocation(location);
        location.setRealtimeWeather(realtimeWeather);

        String bodyContent = mapper.writeValueAsString(realtimeWeather);

        Mockito.when(weatherService.update(locationCode, realtimeWeather)).thenReturn(realtimeWeather);

        String expectedLocation = location.getCityName() + ", " + location.getRegionName() + ", " + location.getCountryName();

        mockMvc.perform(put(requestURI).contentType("application/json").content(bodyContent))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.location", is(expectedLocation)))
                .andDo(print());
    }

}