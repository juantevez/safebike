package com.safe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableCaching
@EnableJpaRepositories({
        "com.safe.bike.infrastructure.persistence",
        "com.safe.user.infrastructure.persistence.repositories",
        "com.safe.loadphoto.infrastructure.persistence",
        "com.safe.location.infrastructure.persistence",
        "com.safe.location.domain.ports",
        "com.safe.user.domain.ports"
})
@EntityScan({
        "com.safe.bike.domain.model",
        "com.safe.user.domain.model.entity",
        "com.safe.loadphoto.domain.model.entity",
        "com.safe.location.domain.model.entity"
})
public class BikeSafeApplication {

    private static final Logger log = LoggerFactory.getLogger(BikeSafeApplication.class);

    public static void main(String[] args) {
        log.info("Iniciando aplicación BikeSafe...");
        SpringApplication.run(BikeSafeApplication.class, args);
    }
}
