package pet.skyapi.weatherforecast.location;

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

    private static LocationDTO generateNewLocationDTO() {
        LocationDTO location = new LocationDTO();
        location.setCode("NYC_USA");
        location.setCityName("New York City");
        location.setRegionName("New York");
        location.setCountryCode("US");
        location.setCountryName("United States of America");
        location.setEnabled(true);
        return location;
    }

    private static Location generateByDto(LocationDTO locationDTO){
        Location location = new Location();
        location.setCode(locationDTO.getCode());
        location.setTrashed(false);
        location.setCityName(locationDTO.getCityName());
        location.setRegionName(locationDTO.getRegionName());
        location.setCountryName(locationDTO.getCountryName());
        location.setCountryCode(locationDTO.getCountryCode());

        return location;
    }

    @Test
    public void testValidateRequestBodyLocationCodeNotNull() throws Exception {
        LocationDTO location = generateNewLocationDTO();
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
        LocationDTO location = generateNewLocationDTO();
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
        LocationDTO location = new LocationDTO();
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
        LocationDTO location = new LocationDTO();

        String bodyContent = mapper.writeValueAsString(location);

        mockMvc.perform(post(END_POINT_PATH).contentType("application/json").content(bodyContent))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    public void testAddShouldReturn201Created() throws Exception {
        LocationDTO locationDTO = generateNewLocationDTO();
        Location location = generateByDto(locationDTO);

        Mockito.when(service.add(location)).thenReturn(location);

        String bodyContent = mapper.writeValueAsString(locationDTO);

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
        LocationDTO locationDto1 = generateNewLocationDTO();
        LocationDTO locationDto2 = generateNewLocationDTO();
        locationDto2.setCode("LACA_USA");
        locationDto2.setRegionName("California");
        locationDto2.setCityName("Los Angeles");

        List<LocationDTO> locationList = Arrays.asList(locationDto1, locationDto2);

        Location location1 = generateByDto(locationDto1);
        Location location2 = generateByDto(locationDto2);

        List<Location> locations = Arrays.asList(location1, location2);
        Mockito.when(service.getList()).thenReturn(locations);

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
        String locationCode = "ABCDEF";
        String requestURI = END_POINT_PATH + "/" + locationCode;

        LocationNotFoundException ex = new LocationNotFoundException(locationCode);

        Mockito.when(service.getLocation(Mockito.anyString())).thenThrow(ex);

        mockMvc.perform(get(requestURI))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    public void testGetShouldReturn200Success() throws Exception {

        LocationDTO dto = generateNewLocationDTO();
        Location location = generateByDto(dto);

        String requestURI = END_POINT_PATH + "/" + dto.getCode();


        Mockito.when(service.getLocation(location.getCode())).thenReturn(location);

        mockMvc.perform(get(requestURI))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.code", is(location.getCode())))
                .andDo(print());
    }

    @Test
    public void testUpdateShouldReturn404NotFound() throws Exception{
        LocationDTO locationDTO = generateNewLocationDTO();
        Location location = generateByDto(locationDTO);
        String bodyContent = mapper.writeValueAsString(locationDTO);

        Mockito.when(service.updateLocation(location)).thenThrow(new LocationNotFoundException(location.getCode()));

        mockMvc.perform(put(END_POINT_PATH).contentType("application/json").content(bodyContent))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    public void testUpdateShouldReturn400BadRequest() throws Exception{
        LocationDTO location = generateNewLocationDTO();
        location.setCode(null);
        String bodyContent = mapper.writeValueAsString(location);

        mockMvc.perform(put(END_POINT_PATH).contentType("application/json").content(bodyContent))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    public void testUpdateShouldReturn200OK() throws Exception{
        LocationDTO locationDTO = generateNewLocationDTO();
        String bodyContent = mapper.writeValueAsString(locationDTO);

        Location location = generateByDto(locationDTO);
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