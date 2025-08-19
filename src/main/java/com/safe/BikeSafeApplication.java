package com.safe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class BikeSafeApplication {

    private static final Logger log = LoggerFactory.getLogger(BikeSafeApplication.class);

    public static void main(String[] args) {
        log.info("Iniciando aplicación BikeSafe...");
        SpringApplication.run(BikeSafeApplication.class, args);
    }
}