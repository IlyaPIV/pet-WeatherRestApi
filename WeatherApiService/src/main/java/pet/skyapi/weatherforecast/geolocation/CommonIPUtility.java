package pet.skyapi.weatherforecast.geolocation;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommonIPUtility {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommonIPUtility.class);
    public static String getIPAddress(HttpServletRequest request){
        String ip = request.getHeader("X-FORWARDED-FOR");

        if (ip == null || ip.isEmpty()){
            ip = request.getRemoteAddr();
        }

        LOGGER.info("Client's IP Address: " + ip);

        return ip;
    }
}
