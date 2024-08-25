package lissa.trading.tinkoff.stock.service;

import lissa.trading.tinkoff.stock.service.config.token.ApiConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
@EnableFeignClients
@EnableConfigurationProperties(ApiConfig.class)
@ComponentScan(basePackages = "lissa.trading")
public class TinkoffStockServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TinkoffStockServiceApplication.class, args);
    }
}
