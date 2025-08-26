package com.safe.bike.infrastructure.web;

import com.safe.bike.domain.model.entity.*;
import com.safe.bike.service.BikeServiceImpl;
import com.safe.user.adapter.out.persistence.entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

@SpringBootTest
class BikeServiceLoadTest {

    @Autowired
    private BikeServiceImpl bikeService;

    @Test
    void testSave100BikesConcurrently() throws InterruptedException {
        int threadCount = 100;
        ExecutorService executor = Executors.newFixedThreadPool(20);
        CountDownLatch latch = new CountDownLatch(threadCount);

        List<Runnable> tasks = IntStream.range(0, threadCount)
                .mapToObj(i -> createBikeTask("SNNNN-" + i, latch))
                .toList();

        // Ejecutar
        tasks.forEach(executor::submit);
        latch.await(2, TimeUnit.MINUTES); // espera máxima

        executor.shutdown();
    }

    private Runnable createBikeTask(String serial, CountDownLatch latch) {
        return () -> {
            try {
                BikeEntity bike = new BikeEntity();
                bike.setSerialNumber(serial);
                bike.setBrand(getBrand());           // obtén de repo o mock
                bike.setBikeType(getBikeType());
                bike.setBikeModel(getBikeModel());
                bike.setSizeBike(getSize());
                bike.setMoneda(getMoneda());
                bike.setPurchaseValue(1000.0 + Math.random() * 2000);
                bike.setPurchaseDate(LocalDate.now());

                // ✅ Asignar el usuario (no puede ser null)
                bike.setUser(getUser());

                bikeService.save(bike); // tu servicio real


                System.out.println("✅ Guardado: " + serial);
            } catch (Exception e) {
                System.err.println("❌ Error en " + serial + ": " + e.getMessage());
            } finally {
                latch.countDown();
            }
        };
    }

    // Dentro de tu clase de prueba: BikeServiceLoadTest.java

    private BrandEntity getBrand() {
        BrandEntity brand = new BrandEntity();
        brand.setBrandId(1L);
        brand.setName("Test Brand");
        //brand.setCreatedAt(LocalDateTime.now());
        return brand;
    }

    private BikeTypeEntity getBikeType() {
        BikeTypeEntity type = new BikeTypeEntity();
        type.setBikeTypeId(1L);
        type.setName("MTB");
        type.setDescription("Mountain Bike");
        type.setCreatedAt(LocalDateTime.now());
        return type;
    }

    private BikeModelEntity getBikeModel() {
        BikeModelEntity model = new BikeModelEntity();
        model.setIdBikeModel(1L);
        model.setModelName("Test Model");
        model.setBrand(getBrand());           // reutiliza el mock
        model.setBikeType(getBikeType());     // reutiliza el mock
        model.setYearReleased(2025);
        model.setCreatedAt(LocalDateTime.now());
        return model;
    }

    private SizeEntity getSize() {
        SizeEntity size = new SizeEntity();
        size.setId(1);
        size.setSigla("M");
        size.setDescription("Mediano");
        return size;
    }

    private MonedaEntity getMoneda() {
        MonedaEntity moneda = new MonedaEntity();
        moneda.setId(1);
        moneda.setCodigoMoneda("USD");
        //moneda.setDescripcion("Dólar estadounidense");
        return moneda;
    }

    private UserEntity getUser() {
        UserEntity user = new UserEntity();
        user.setId(10L); // Ajusta al ID de un usuario existente o válido
        user.setUsername("coco");
        user.setEmail("coco@me.com");
        user.setCreatedAt(LocalDateTime.now());
        return user;
    }
}