package pet.skyapi.weatherforecast;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import pet.skyapi.weatherforecast.common.HourlyWeather;
import pet.skyapi.weatherforecast.hourly.HourlyWeatherDTO;

@SpringBootApplication
public class WeatherApiServiceApplication {

	@Bean
	public ModelMapper getModelMapper(){
		ModelMapper mapper = new ModelMapper();
		mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

		var typeMap2Dto = mapper.typeMap(HourlyWeather.class, HourlyWeatherDTO.class);
		typeMap2Dto.addMapping(src -> src.getId().getHourOfDay(), HourlyWeatherDTO::setHourOfDay);

		var typeMap2Entity = mapper.typeMap(HourlyWeatherDTO.class, HourlyWeather.class);
		typeMap2Entity.addMapping(HourlyWeatherDTO::getHourOfDay,
									(dest, value) -> dest.getId().setHourOfDay(value !=null ? (int) value : 0));

		return mapper;
	}
	public static void main(String[] args) {
		SpringApplication.run(WeatherApiServiceApplication.class, args);
	}

}
