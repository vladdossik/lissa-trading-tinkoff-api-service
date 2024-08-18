package lissa.trading.tinkoff_stock_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class TinkoffStockServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TinkoffStockServiceApplication.class, args);
    }

}
