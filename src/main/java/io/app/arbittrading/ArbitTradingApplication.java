package io.app.arbittrading;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableFeignClients
@EnableScheduling
@EnableAsync
@PropertySource("classpath:/dev.env")
public class ArbitTradingApplication {

	public static void main(String[] args) {
		SpringApplication.run(ArbitTradingApplication.class, args);
	}

}
