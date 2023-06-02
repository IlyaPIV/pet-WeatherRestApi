package pet.skyapi.weatherforecast.geolocation;

import com.ip2location.IP2Location;
import com.ip2location.IPResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pet.skyapi.weatherforecast.common.Location;

import java.io.IOException;
import java.io.InputStream;

@Service
public class GeolocationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(GeolocationService.class);
    private final String DB_PATH = "/ip2locdb/IP2LOCATION-LITE-DB3.BIN";
    private IP2Location ipLocator = new IP2Location();

    public GeolocationService() {
        try {
            InputStream inputStream = getClass().getResourceAsStream(DB_PATH);
            byte[] data = inputStream.readAllBytes();
            ipLocator.Open(data);
            inputStream.close();
        } catch (IOException ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    public Location getLocation(String ipAddress) throws GeolocationException {
        try {
            IPResult ipResult = ipLocator.IPQuery(ipAddress);

            if(!"OK".equals(ipResult.getStatus())){
                throw new GeolocationException("Geolocation failed with status: " + ipResult.getStatus());
            }

            return new Location(ipResult.getCity(), ipResult.getRegion(), ipResult.getCountryLong(), ipResult.getCountryShort());
        } catch (IOException e) {
            throw new GeolocationException("Error querying IP database");
        }
    }
}
