package pet.skyapi.weatherforecast.location;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import pet.skyapi.weatherforecast.common.Location;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LocationApiController.class)
class LocationApiControllerTest {

    private static final String END_POINT_PATH = "/v1/locations";

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper mapper;
    @MockBean
    LocationService service;

    private static Location generateNewLocation() {
        Location location = new Location();
        location.setCode("NYC_USA");
        location.setCityName("New York City");
        location.setRegionName("New York");
        location.setCountryCode("US");
        location.setCountryName("United States of America");
        location.setEnabled(true);
        location.setTrashed(false);
        return location;
    }

    @Test
    public void testValidateRequestBodyLocationCodeNotNull() throws Exception {
        Location location = generateNewLocation();
        location.setCode(null);

        String bodyContent = mapper.writeValueAsString(location);

        mockMvc.perform(post(END_POINT_PATH).contentType("application/json").content(bodyContent))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.errors[0]", is("Location code cannot be null")))
                .andDo(print());
    }

    @Test
    public void testValidateRequestBodyLocationCodeLength() throws Exception {
        Location location = generateNewLocation();
        location.setCode("BY");

        String bodyContent = mapper.writeValueAsString(location);

        mockMvc.perform(post(END_POINT_PATH).contentType("application/json").content(bodyContent))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.errors[0]", is("Location code must have 3-12 characters")))
                .andDo(print());
    }

    @Test
    public void testValidateRequestBodyAllFieldInvalid() throws Exception {
        Location location = new Location();
        location.setRegionName("");

        String bodyContent = mapper.writeValueAsString(location);

        MvcResult mvcResult = mockMvc.perform(post(END_POINT_PATH).contentType("application/json").content(bodyContent))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json"))
                .andDo(print())
                .andReturn();

        String responseBody = mvcResult.getResponse().getContentAsString();

        assertThat(responseBody).contains("Location code cannot be null");
        assertThat(responseBody).contains("City name cannot be null");
        assertThat(responseBody).contains("Country name cannot be null");
        assertThat(responseBody).contains("Country code cannot be null");
        assertThat(responseBody).contains("Region name must have 3-128 characters");
    }

    @Test
    public void testAddShouldReturn400BadRequest() throws Exception {
        Location location = new Location();

        String bodyContent = mapper.writeValueAsString(location);

        mockMvc.perform(post(END_POINT_PATH).contentType("application/json").content(bodyContent))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    public void testAddShouldReturn201Created() throws Exception {
        Location location = generateNewLocation();

        Mockito.when(service.add(location)).thenReturn(location);

        String bodyContent = mapper.writeValueAsString(location);

        mockMvc.perform(post(END_POINT_PATH).contentType("application/json").content(bodyContent))
                .andExpect(status().isCreated())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.code", is("NYC_USA")))
                .andExpect(jsonPath("$.city_name", is("New York City")))
                .andExpect(header().string("Location", "/v1/locations/NYC_USA"))
                .andDo(print());
    }

    @Test
    public void testGetListShouldReturn204NoContent() throws Exception {

        Mockito.when(service.getList()).thenReturn(Collections.emptyList());

        mockMvc.perform(get(END_POINT_PATH))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    public void testGetListShouldReturn200OK() throws Exception{
        Location location1 = generateNewLocation();
        Location location2 = generateNewLocation();
        location2.setCode("LACA_USA");
        location2.setRegionName("California");
        location2.setCityName("Los Angeles");

        List<Location> locationList = Arrays.asList(location1, location2);

        Mockito.when(service.getList()).thenReturn(locationList);

        mockMvc.perform(get(END_POINT_PATH))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andDo(print());
    }

    @Test
    public void testPostShouldReturn405MethodNotAllowed() throws Exception {
        String requestURI = END_POINT_PATH + "/ABCDEF";

        mockMvc.perform(post(requestURI))
                .andExpect(status().isMethodNotAllowed())
                .andDo(print());
    }

    @Test
    public void testGetShouldReturn404MethodNotFound() throws Exception {
        String requestURI = END_POINT_PATH + "/ABCDEF";

        mockMvc.perform(get(requestURI))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    public void testGetShouldReturn200Success() throws Exception {

        Location location = generateNewLocation();
        String requestURI = END_POINT_PATH + "/" + location.getCode();

        Mockito.when(service.getLocation(location.getCode())).thenReturn(location);

        mockMvc.perform(get(requestURI))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.code", is(location.getCode())))
                .andDo(print());
    }

    @Test
    public void testUpdateShouldReturn404NotFound() throws Exception{
        Location location = generateNewLocation();
        String bodyContent = mapper.writeValueAsString(location);

        Mockito.when(service.updateLocation(location)).thenThrow(new LocationNotFoundException("exception in test"));

        mockMvc.perform(put(END_POINT_PATH).contentType("application/json").content(bodyContent))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    public void testUpdateShouldReturn400BadRequest() throws Exception{
        Location location = generateNewLocation();
        location.setCode(null);
        String bodyContent = mapper.writeValueAsString(location);

        mockMvc.perform(put(END_POINT_PATH).contentType("application/json").content(bodyContent))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    public void testUpdateShouldReturn200OK() throws Exception{
        Location location = generateNewLocation();
        String bodyContent = mapper.writeValueAsString(location);

        Mockito.when(service.updateLocation(location)).thenReturn(location);

        mockMvc.perform(put(END_POINT_PATH).contentType("application/json").content(bodyContent))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.code", is("NYC_USA")))
                .andExpect(jsonPath("$.city_name", is("New York City")))
                .andDo(print());
    }

    @Test
    public void testDeleteShouldReturn404NotFound() throws Exception{
        String code = "ABCDEF";
        String requestURI = END_POINT_PATH + "/" + code;

        Mockito.doThrow(LocationNotFoundException.class).when(service).deleteLocation(code);

        mockMvc.perform(delete(requestURI))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    public void testDeleteShouldReturn204NoContent() throws Exception{
        String code = "ABCDEF";
        String requestURI = END_POINT_PATH + "/" + code;

        Mockito.doNothing().when(service).deleteLocation(code);

        mockMvc.perform(delete(requestURI))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

}