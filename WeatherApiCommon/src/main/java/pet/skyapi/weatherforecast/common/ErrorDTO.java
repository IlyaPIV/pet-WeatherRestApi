package pet.skyapi.weatherforecast.common;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class ErrorDTO {
    private Date timestamp;
    private int status;
    private String path;
    private String  error;
}
