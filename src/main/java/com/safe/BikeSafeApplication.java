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
        "com.safe.user.adapter.out.persistence.repository",
        "com.safe.loadphoto.infrastructure.persistence"
})
@EntityScan({
        "com.safe.bike.domain.model.entity",
        "com.safe.user.adapter.out.persistence.entity",
        "package com.safe.loadphoto.domain.model.entity"
})
public class BikeSafeApplication {

    private static final Logger log = LoggerFactory.getLogger(BikeSafeApplication.class);

    public static void main(String[] args) {
        log.info("Iniciando aplicación BikeSafe...");
        SpringApplication.run(BikeSafeApplication.class, args);
    }
}
