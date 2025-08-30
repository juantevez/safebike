package com.safe.loadphoto.config;


import com.safe.loadphoto.domain.port.in.PhotoExifServicePort;
import com.safe.loadphoto.domain.port.out.PhotoExifRepositoryPort;
import com.safe.loadphoto.domain.port.out.PhotoFileRepositoryPort;
import com.safe.loadphoto.infrastructure.adapter.ExifAdapter;
import com.safe.loadphoto.service.PhotoExifService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PhotoLoadingConfig {

    @Bean
    public PhotoExifServicePort photoExifService(
            ExifAdapter exifAdapter,
            PhotoExifRepositoryPort exifRepositoryPort,
            PhotoFileRepositoryPort photoFileRepositoryPort
    ) {
        return new PhotoExifService(exifAdapter, exifRepositoryPort, photoFileRepositoryPort);
    }

}
