package pet.skyapi.weatherforecast.common;

import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class HourlyWeatherId implements Serializable {
    private int hourOfDay;

    @ManyToOne
    @JoinColumn(name = "location_code")
    private Location location;

}
