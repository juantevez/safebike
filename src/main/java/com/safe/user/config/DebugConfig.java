package com.safe.user.config;

import com.safe.user.infrastructure.adapters.output.persistence.repository.UserRepository;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

import java.util.Arrays;

@Configuration
public class DebugConfig {

    @EventListener
    public void onApplicationReady(ApplicationReadyEvent event) {
        System.out.println("🔍 Verificando beans de User...");

        ApplicationContext context = event.getApplicationContext();

        try {
            UserRepository repo = context.getBean(UserRepository.class);
            System.out.println("✅ UserRepository encontrado: " + repo.getClass().getName());
        } catch (Exception e) {
            System.out.println("❌ UserRepository NO encontrado: " + e.getMessage());
        }

        System.out.println("🔍 Beans que contienen 'user':");
        Arrays.stream(context.getBeanDefinitionNames())
                .filter(name -> name.toLowerCase().contains("user"))
                .forEach(System.out::println);
    }
}
