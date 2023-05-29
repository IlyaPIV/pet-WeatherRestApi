package pet.skyapi.weatherforecast.common;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

@Entity
@Getter
@Setter
@Table(name = "weather_hourly")
public class HourlyWeather {
    @Id
    private HourlyWeatherId id = new HourlyWeatherId();

    @Range(min = -50, max = 50, message = "Temperature must be in range of -50 to 50 Celsius degree")
    private int temperature;

    @Range(min = 0, max = 100, message = "Precipitation must be in range of 0 to 100 percentage")
    private int precipitation;

    @Column(length = 50)
    @NotBlank(message = "Status must not be empty")
    @Length(min = 3, max = 50, message = "Status must be in between 3-50 characters")
    private String status;



    public HourlyWeather temperature(int temp) {
        setTemperature(temp);
        return this;
    }

    public HourlyWeather id(Location location, int hour){
        this.id.setLocation(location);
        this.id.setHourOfDay(hour);
        return this;
    }

    public HourlyWeather precipitation(int precipitation){
        setPrecipitation(precipitation);
        return this;
    }

    public HourlyWeather status(String status){
        setStatus(status);
        return this;
    }

    public HourlyWeather location(Location location){
        this.id.setLocation(location);
        return this;
    }

    public HourlyWeather hourOfDay(int hour){
        this.id.setHourOfDay(hour);
        return this;
    }
}
