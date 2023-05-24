package pet.skyapi.weatherforecast.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.Hibernate;

import java.util.Objects;

@Entity
@Table(name = "locations")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Location {
    @Id
    @Column(length = 12, nullable = false, unique = true)
    @NotBlank(message = "Location code cannot be left blank")
    private String code;

    @Column(length = 128, nullable = false)
    @JsonProperty("city_name")
    @NotBlank(message = "City name cannot be left blank")
    private String cityName;

    @Column(length = 128)
    @JsonProperty("region_name")
    private String regionName;

    @Column(length = 64, nullable = false)
    @JsonProperty("country_name")
    @NotBlank(message = "Country name cannot be left blank")
    private String countryName;

    @Column(length = 2, nullable = false)
    @JsonProperty("country_code")
    @NotBlank(message = "Country code cannot be left blank")
    private String countryCode;
    @Column
    private boolean enabled;
    @Column
    @JsonIgnore
    private boolean trashed;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Location location = (Location) o;
        return getCode() != null && Objects.equals(getCode(), location.getCode());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
