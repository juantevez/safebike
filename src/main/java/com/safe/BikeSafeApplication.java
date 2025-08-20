package com.safe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableCaching
@EnableJpaRepositories(basePackages = {
        "com.safe.user.infrastructure.persistence",
        "com.safe.bike.infrastructure.persistence"
})
public class BikeSafeApplication {

    private static final Logger log = LoggerFactory.getLogger(BikeSafeApplication.class);

    public static void main(String[] args) {
        log.info("Iniciando aplicación BikeSafe...");
        SpringApplication.run(BikeSafeApplication.class, args);
    }
}