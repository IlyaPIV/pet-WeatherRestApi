package pet.skyapi.weatherforecast.hourly;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import pet.skyapi.weatherforecast.common.HourlyWeather;
import pet.skyapi.weatherforecast.common.Location;
import pet.skyapi.weatherforecast.geolocation.GeolocationException;
import pet.skyapi.weatherforecast.geolocation.GeolocationService;
import pet.skyapi.weatherforecast.location.LocationNotFoundException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HourlyWeatherController.class)
class HourlyWeatherControllerTest {


    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private HourlyWeatherService hourlyWeatherService;
    @MockBean
    private GeolocationService geolocationService;

    private final static String END_POINT_PATH = "/v1/hourly";
    public static final String X_CURRENT_HOUR = "X-Current-Hour";

    @Test
    public void testGetByIPShouldReturn400BadRequestBecauseNoHeaderXCurrentHour() throws Exception {
        mockMvc.perform(get(END_POINT_PATH))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    public void testGetByIPShouldReturn400BadRequestBecauseGeolocationException() throws Exception {


        Mockito.when((geolocationService.getLocation(Mockito.anyString()))).thenThrow(GeolocationException.class);

        mockMvc.perform(get(END_POINT_PATH).header(X_CURRENT_HOUR, "10"))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    public void testGetByIPShouldReturn204NoContent() throws Exception {
        int currentHour = 9;
        Location location = new Location();
        location.setCode("NYC_USA");

        Mockito.when((geolocationService.getLocation(Mockito.anyString()))).thenReturn(location);
        Mockito.when(hourlyWeatherService.getHourlyWeatherByLocation(location, currentHour)).thenReturn(new ArrayList<>());

        mockMvc.perform(get(END_POINT_PATH).header(X_CURRENT_HOUR, String.valueOf(currentHour)))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    public void testGetByIPShouldReturn200Ok() throws Exception {
        int currentHour = 9;
        Location location = new Location();
        location.setCode("NYC_USA");
        location.setCityName("New York City");
        location.setRegionName("New York");
        location.setCountryCode("US");
        location.setCountryName("United States of America");

        HourlyWeather forecast1 = new HourlyWeather().location(location)
                                                    .hourOfDay(currentHour + 1)
                                                    .temperature(13)
                                                    .precipitation(70)
                                                    .status("Snowy");

        HourlyWeather forecast2 = new HourlyWeather().location(location)
                                                    .hourOfDay(currentHour + 2)
                                                    .temperature(14)
                                                    .precipitation(75)
                                                    .status("Snowy");


        Mockito.when((geolocationService.getLocation(Mockito.anyString()))).thenReturn(location);
        Mockito.when(hourlyWeatherService.getHourlyWeatherByLocation(location, currentHour)).thenReturn(List.of(forecast1, forecast2));

        String expectedLocation = location.toString();

        mockMvc.perform(get(END_POINT_PATH).header(X_CURRENT_HOUR, String.valueOf(currentHour)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.location", is(expectedLocation)))
                .andExpect(jsonPath("$.hourly_forecast[0].hour_of_day", is(currentHour + 1)))
                .andExpect(jsonPath("$.hourly_forecast[1].hour_of_day", is(currentHour + 2)))
                .andDo(print());
    }


    @Test
    public void testGetByLocationCodeShouldReturn400BadRequest() throws Exception{
        String locationCode = "NYC_USA";
        String requestURI = END_POINT_PATH + "/" + locationCode;


        mockMvc.perform(get(requestURI))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    public void testGetByLocationCodeShouldReturn404NotFound() throws Exception{
        int currentHour = 9;
        String locationCode = "NYC_USA";
        String requestURI = END_POINT_PATH + "/" + locationCode;

        Mockito.when(hourlyWeatherService.getHourlyWeatherByLocationCode(locationCode, currentHour))
                .thenThrow(LocationNotFoundException.class);

        mockMvc.perform(get(requestURI).header(X_CURRENT_HOUR, currentHour))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    public void testGetByLocationCodeShouldReturn204NoContent() throws Exception{
        int currentHour = 19;
        String locationCode = "NYC_USA";
        String requestURI = END_POINT_PATH + "/" + locationCode;

        Mockito.when(hourlyWeatherService.getHourlyWeatherByLocationCode(locationCode, currentHour))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get(requestURI).header(X_CURRENT_HOUR, currentHour))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    public void testGetByLocationCodeShouldReturn200Ok() throws Exception{
        int currentHour = 19;
        String locationCode = "NYC_USA";
        String requestURI = END_POINT_PATH + "/" + locationCode;

        Location location = new Location();
        location.setCode(locationCode);
        location.setCityName("New York City");
        location.setRegionName("New York");
        location.setCountryCode("US");
        location.setCountryName("United States of America");

        HourlyWeather forecast1 = new HourlyWeather().location(location)
                .hourOfDay(currentHour + 1)
                .temperature(13)
                .precipitation(70)
                .status("Snowy");

        HourlyWeather forecast2 = new HourlyWeather().location(location)
                .hourOfDay(currentHour + 2)
                .temperature(14)
                .precipitation(75)
                .status("Snowy");

        Mockito.when(hourlyWeatherService.getHourlyWeatherByLocationCode(locationCode, currentHour)).thenReturn(List.of(forecast1, forecast2));

        String expectedLocation = location.toString();

        mockMvc.perform(get(requestURI).header(X_CURRENT_HOUR, String.valueOf(currentHour)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.location", is(expectedLocation)))
                .andExpect(jsonPath("$.hourly_forecast[0].hour_of_day", is(currentHour + 1)))
                .andExpect(jsonPath("$.hourly_forecast[1].hour_of_day", is(currentHour + 2)))
                .andDo(print());
    }

    @Test
    public void testUpdateShouldReturn400BadRequestBecauseNoData() throws Exception{
        String locationCode = "NYC_USA";
        String requestURI = END_POINT_PATH + "/" + locationCode;

        List<HourlyWeatherDTO> listDTO = Collections.emptyList();

        String requestBody = objectMapper.writeValueAsString(listDTO);

        mockMvc.perform(put(requestURI).contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]", is("Hourly forecast data cannot be empty")))
                .andDo(print());
    }

    @Test
    public void testUpdateShouldReturn400BadRequestBecauseInvalidData() throws Exception{
        String locationCode = "NYC_USA";
        String requestURI = END_POINT_PATH + "/" + locationCode;

                HourlyWeatherDTO forecast1 = new HourlyWeatherDTO()
                .hourOfDay(11)
                .temperature(134)
                .precipitation(70)
                .status("Snowy");

        HourlyWeatherDTO forecast2 = new HourlyWeatherDTO()
                .hourOfDay(12)
                .temperature(14)
                .precipitation(75)
                .status("Sunny");

        List<HourlyWeatherDTO> listDTO = List.of(forecast1, forecast2);

        String requestBody = objectMapper.writeValueAsString(listDTO);

        mockMvc.perform(put(requestURI).contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]", containsString("Temperature must be in range of -50 to 50 Celsius degree")))
                .andDo(print());
    }

    @Test
    public void testUpdateShouldReturn404NotFound() throws Exception{
        String locationCode = "NYC_USA";
        String requestURI = END_POINT_PATH + "/" + locationCode;

        HourlyWeatherDTO forecast1 = new HourlyWeatherDTO()
                .hourOfDay(11)
                .temperature(13)
                .precipitation(70)
                .status("Snowy");

        HourlyWeatherDTO forecast2 = new HourlyWeatherDTO()
                .hourOfDay(12)
                .temperature(14)
                .precipitation(75)
                .status("Sunny");

        List<HourlyWeatherDTO> listDTO = List.of(forecast1, forecast2);

        String requestBody = objectMapper.writeValueAsString(listDTO);

        Mockito.when(hourlyWeatherService.updateByLocationCode(Mockito.eq(locationCode), Mockito.anyList()))
                .thenThrow(LocationNotFoundException.class);

        mockMvc.perform(put(requestURI).contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    public void testUpdateShouldReturn200Ok() throws Exception{
        String locationCode = "NYC_USA";
        String requestURI = END_POINT_PATH + "/" + locationCode;

        //REQUEST
        HourlyWeatherDTO forecastDto1 = new HourlyWeatherDTO()
                .hourOfDay(11)
                .temperature(13)
                .precipitation(70)
                .status("Snowy");

        HourlyWeatherDTO forecastDto2 = new HourlyWeatherDTO()
                .hourOfDay(12)
                .temperature(14)
                .precipitation(75)
                .status("Sunny");

        //RESPONSE
        Location location = new Location();
        location.setCode(locationCode);
        location.setCityName("New York City");
        location.setRegionName("New York");
        location.setCountryCode("US");
        location.setCountryName("United States of America");

        HourlyWeather forecast1 = new HourlyWeather().location(location)
                                                    .hourOfDay(11)
                                                    .temperature(13)
                                                    .precipitation(70)
                                                    .status("Snowy");

        HourlyWeather forecast2 = new HourlyWeather().location(location)
                                                    .hourOfDay(12)
                                                    .temperature(14)
                                                    .precipitation(75)
                                                    .status("Sunny");


        List<HourlyWeatherDTO> listDTO = List.of(forecastDto1, forecastDto2);
        List<HourlyWeather> updatedForecast = List.of(forecast1, forecast2);

        String requestBody = objectMapper.writeValueAsString(listDTO);

        Mockito.when(hourlyWeatherService.updateByLocationCode(Mockito.eq(locationCode), Mockito.anyList()))
                .thenReturn(updatedForecast);

        mockMvc.perform(put(requestURI).contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.location", is(location.toString())))
                .andExpect(jsonPath("$.hourly_forecast[0].hour_of_day", is(11)))
                .andExpect(jsonPath("$.hourly_forecast[1].hour_of_day", is(12)))
                .andDo(print());
    }
}