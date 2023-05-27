package pet.skyapi.weatherforecast.realtime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class RealtimeWeatherDTO {

    private String location;
    private int temperature;
    private int humidity;
    private int precipitation;
    @JsonProperty("wind_speed")
    private int windSpeed;
    private String status;
    @JsonProperty("last_update")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private Date lastUpdate;

}
