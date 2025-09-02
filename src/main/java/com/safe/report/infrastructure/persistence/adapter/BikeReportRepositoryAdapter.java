package com.safe.report.infrastructure.persistence.adapter;

import com.safe.bike.domain.model.Bike;
import com.safe.bike.domain.model.entity.BikeEntity;
import com.safe.bike.infrastructure.persistence.bike.BikeJpaRepository;
import com.safe.report.domain.port.out.BikeReportRepository;
import com.safe.user.domain.model.User;
import com.safe.user.infrastructure.adapters.output.persistence.entities.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;


@Component
@RequiredArgsConstructor
public class BikeReportRepositoryAdapter implements BikeReportRepository {

    private final BikeJpaRepository bikeJpaRepository;

    @Override
    public List<Bike> findAllBikesWithUserInfo() {
        List<BikeEntity> bikeEntities = bikeJpaRepository.findAllBikesWithUserInfo();
        return bikeEntities.stream()
                .map(this::mapToDomain)
                .collect(Collectors.toList());
    }

    @Override
    public int countTotalUsers() {
        return bikeJpaRepository.countDistinctUsers();
    }

    private Bike mapToDomain(BikeEntity bikeEntity) {

            return Bike.builder()
                    .id(bikeEntity.getBikeId())
                    //.user(bikeEntity.getUser() != null ? new Bike.User(bikeEntity.getUser().getId(), bikeEntity.getUser().getEmail()) : null)
                    .brand(bikeEntity.getBrand() != null ? new Bike.Brand(bikeEntity.getBrand().getBrandId(), bikeEntity.getBrand().getName()) : null)
                    .bikeType(bikeEntity.getBikeType() != null ? new Bike.BikeType(bikeEntity.getBikeType().getBikeTypeId(), bikeEntity.getBikeType().getName()) : null)
                    .bikeModel(bikeEntity.getBikeModel() != null ? new Bike.BikeModel(bikeEntity.getBikeModel().getIdBikeModel(), bikeEntity.getBikeModel().getModelName()) : null)
                    .serialNumber(bikeEntity.getSerialNumber())
                    .purchaseDate(bikeEntity.getPurchaseDate())
                    .size(bikeEntity.getSizeBike() != null ? new Bike.Size(bikeEntity.getSizeBike().getId(), bikeEntity.getSizeBike().getSigla()) : null)
                    .currency(bikeEntity.getMoneda() != null ? new Bike.Currency(bikeEntity.getMoneda().getId(), bikeEntity.getMoneda().getNombreMoneda(), bikeEntity.getMoneda().getCodigoMoneda()) : null)
                    .purchaseValue(bikeEntity.getPurchaseValue())
                    .createdAt(bikeEntity.getCreatedAt())
                    .location(null) // Asumiendo que BikeEntity no tiene un campo Location; ajustar si es necesario
                    .build();
    }

    private User mapUserToDomain(UserEntity userEntity) {
        return User.builder()
                .id(userEntity.getId())
                .username(userEntity.getUsername())
                .email(userEntity.getEmail())
                .password(userEntity.getPassword())
                .firstName(userEntity.getFirstName())
                .lastName(userEntity.getLastName())
                .role(userEntity.getRole())
                .createdAt(userEntity.getCreatedAt())
                .build();
    }
}