package pet.skyapi.weatherforecast;

import com.ip2location.IP2Location;
import com.ip2location.IPResult;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class IP2LocationTest {
    private final String DBPath = "ip2locdb/IP2LOCATION-LITE-DB3.BIN";

    @Test
    public void testInvalidIP() throws IOException {
        IP2Location ipLocator = new IP2Location();
        ipLocator.Open(DBPath);

        String ipAddress = "wrongAddress";
        IPResult result = ipLocator.IPQuery(ipAddress);

        assertThat(result.getStatus()).isEqualTo("INVALID_IP_ADDRESS");

        System.out.println(result);
    }

    @Test
    public void testValidIP() throws IOException{
        IP2Location ipLocator = new IP2Location();
        ipLocator.Open(DBPath);

        String ipAddress = "108.30.178.78"; //New York
        IPResult result = ipLocator.IPQuery(ipAddress);

        assertThat(result.getStatus()).isEqualTo("OK");
        assertThat(result.getCity()).isEqualTo("New York City");

        System.out.println(result);
    }
}
