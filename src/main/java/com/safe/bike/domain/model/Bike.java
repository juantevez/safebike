package com.safe.bike.domain.model;

import com.safe.user.domain.model.User;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Domain model representing a Bike
 * This class represents the core business entity for bikes in the domain layer
 */
@Builder
public class Bike {

        private Long id;
        private User user;
        private Brand brand;
        private BikeType bikeType;
        private BikeModel bikeModel;
        private String serialNumber;
        private LocalDate purchaseDate;
        private Size size;
        private Currency currency;
        private double purchaseValue;
        private LocalDateTime createdAt;
        private Location location;

        // Default constructor
        public Bike() {}

        // Constructor with required fields
        public Bike(Long id, User user, String serialNumber) {
                this.id = id;
                this.user = user;
                this.serialNumber = serialNumber;
        }

        // Full constructor
        public Bike(Long id, User user, Brand brand, BikeType bikeType, BikeModel bikeModel,
                    String serialNumber, LocalDate purchaseDate, Size size, Currency currency,
                    double purchaseValue, LocalDateTime createdAt, Location location) {
                this.id = id;
                this.user = user;
                this.brand = brand;
                this.bikeType = bikeType;
                this.bikeModel = bikeModel;
                this.serialNumber = serialNumber;
                this.purchaseDate = purchaseDate;
                this.size = size;
                this.currency = currency;
                this.purchaseValue = purchaseValue;
                this.createdAt = createdAt;
                this.location = location;
        }

        // Getters and Setters
        public Long getId() {
                return id;
        }

        public void setId(Long id) {
                this.id = id;
        }

        public User getUser() {
                return user;
        }

        public void setUser(User user) {
                this.user = user;
        }

        public Brand getBrand() {
                return brand;
        }

        public void setBrand(Brand brand) {
                this.brand = brand;
        }

        public BikeType getBikeType() {
                return bikeType;
        }

        public void setBikeType(BikeType bikeType) {
                this.bikeType = bikeType;
        }

        public BikeModel getBikeModel() {
                return bikeModel;
        }

        public void setBikeModel(BikeModel bikeModel) {
                this.bikeModel = bikeModel;
        }

        public String getSerialNumber() {
                return serialNumber;
        }

        public void setSerialNumber(String serialNumber) {
                this.serialNumber = serialNumber;
        }

        public LocalDate getPurchaseDate() {
                return purchaseDate;
        }

        public void setPurchaseDate(LocalDate purchaseDate) {
                this.purchaseDate = purchaseDate;
        }

        public Size getSize() {
                return size;
        }

        public void setSize(Size size) {
                this.size = size;
        }

        public Currency getCurrency() {
                return currency;
        }

        public void setCurrency(Currency currency) {
                this.currency = currency;
        }

        public double getPurchaseValue() {
                return purchaseValue;
        }

        public void setPurchaseValue(double purchaseValue) {
                this.purchaseValue = purchaseValue;
        }

        public LocalDateTime getCreatedAt() {
                return createdAt;
        }

        public void setCreatedAt(LocalDateTime createdAt) {
                this.createdAt = createdAt;
        }

        public Location getLocation() {
                return location;
        }

        public void setLocation(Location location) {
                this.location = location;
        }

        // Business methods

        /**
         * Gets the full display name of the bike combining brand and model
         * @return formatted string with brand and model information
         */
        public String getDisplayName() {
                StringBuilder sb = new StringBuilder();

                if (brand != null && brand.getName() != null) {
                        sb.append(brand.getName());
                }

                if (bikeModel != null && bikeModel.getName() != null) {
                        if (sb.length() > 0) {
                                sb.append(" ");
                        }
                        sb.append(bikeModel.getName());
                }

                return sb.length() > 0 ? sb.toString() : "Bicicleta";
        }

        /**
         * Gets a complete description including brand, model, type and serial
         * @return formatted description string
         */
        public String getFullDescription() {
                StringBuilder sb = new StringBuilder();

                sb.append(getDisplayName());

                if (bikeType != null && bikeType.getName() != null) {
                        sb.append(" (").append(bikeType.getName()).append(")");
                }

                if (serialNumber != null && !serialNumber.trim().isEmpty()) {
                        sb.append(" - Serial: ").append(serialNumber);
                }

                return sb.toString();
        }

        /**
         * Checks if the bike has complete basic information
         * @return true if has brand, model, and serial number
         */
        public boolean hasCompleteBasicInfo() {
                return brand != null && bikeModel != null &&
                        serialNumber != null && !serialNumber.trim().isEmpty();
        }

        /**
         * Checks if the bike has location information
         * @return true if has location data
         */
        public boolean hasLocationInfo() {
                return location != null && location.hasCompleteAddress();
        }

        /**
         * Gets the age of the bike in years based on purchase date
         * @return age in years, or null if no purchase date
         */
        public Integer getAgeInYears() {
                if (purchaseDate == null) {
                        return null;
                }
                return LocalDate.now().getYear() - purchaseDate.getYear();
        }

        /**
         * Formats the purchase value with currency symbol
         * @return formatted price string
         */
        public String getFormattedPrice() {
                if (currency == null) {
                        return String.format("%.2f", purchaseValue);
                }
                return currency.getSymbol() + " " + String.format("%.2f", purchaseValue);
        }

        @Override
        public boolean equals(Object obj) {
                if (this == obj) return true;
                if (obj == null || getClass() != obj.getClass()) return false;

                Bike bike = (Bike) obj;
                return id != null && id.equals(bike.id);
        }

        @Override
        public int hashCode() {
                return id != null ? id.hashCode() : 0;
        }

        @Override
        public String toString() {
                return "Bike{" +
                        "id=" + id +
                        ", serialNumber='" + serialNumber + '\'' +
                        ", brand=" + (brand != null ? brand.getName() : "null") +
                        ", model=" + (bikeModel != null ? bikeModel.getName() : "null") +
                        ", user=" + (user != null ? user.getEmail() : "null") +
                        '}';
        }

        // Inner classes for related entities (Value Objects)

        public static class User {
                private Long id;
                private String username;
                private String email;
                private String password;
                private String firstName;
                private String lastName;
                private String role = "USER"; // valor por defecto
                private LocalDateTime createdAt;

                public Long getId() {
                        return id;
                }

                public void setId(Long id) {
                        this.id = id;
                }

                public String getUsername() {
                        return username;
                }

                public void setUsername(String username) {
                        this.username = username;
                }

                public String getEmail() {
                        return email;
                }

                public void setEmail(String email) {
                        this.email = email;
                }

                public String getFirstName() {
                        return firstName;
                }

                public void setFirstName(String firstName) {
                        this.firstName = firstName;
                }

                public String getLastName() {
                        return lastName;
                }

                public void setLastName(String lastName) {
                        this.lastName = lastName;
                }

                public LocalDateTime getCreatedAt() {
                        return createdAt;
                }

                public void setCreatedAt(LocalDateTime createdAt) {
                        this.createdAt = createdAt;
                }

                public User(Long id, String email) {
                        this.id = id;
                        this.email = email;
                }
        }

        public static class Brand {
                private Long id;
                private String name;

                public Brand() {}

                public Brand(Long id, String name) {
                        this.id = id;
                        this.name = name;
                }

                public Long getId() { return id; }
                public void setId(Long id) { this.id = id; }
                public String getName() { return name; }
                public void setName(String name) { this.name = name; }
        }

        public static class BikeType {
                private Long id;
                private String name;

                public BikeType() {}

                public BikeType(Long id, String name) {
                        this.id = id;
                        this.name = name;
                }

                public Long getId() { return id; }
                public void setId(Long id) { this.id = id; }
                public String getName() { return name; }
                public void setName(String name) { this.name = name; }
        }

        public static class BikeModel {
                private Long id;
                private String name;

                public BikeModel() {}

                public BikeModel(Long id, String name) {
                        this.id = id;
                        this.name = name;
                }

                public Long getId() { return id; }
                public void setId(Long id) { this.id = id; }
                public String getName() { return name; }
                public void setName(String name) { this.name = name; }
        }

        public static class Size {
                private Long id;
                private String name;

                public Size() {}

                public Size(Long id, String name) {
                        this.id = id;
                        this.name = name;
                }

                public Long getId() { return id; }
                public void setId(Long id) { this.id = id; }
                public String getName() { return name; }
                public void setName(String name) { this.name = name; }
        }

        public static class Currency {
                private Integer id;
                private String name;
                private String symbol;

                public Currency() {}

                public Currency(Integer id, String name, String symbol) {
                        this.id = id;
                        this.name = name;
                        this.symbol = symbol;
                }

                public Integer getId() { return id; }
                public void setId(Integer id) { this.id = id; }
                public String getName() { return name; }
                public void setName(String name) { this.name = name; }
                public String getSymbol() { return symbol; }
                public void setSymbol(String symbol) { this.symbol = symbol; }
        }

        public static class Location {
                private Long localidadId;
                private String localidadName;
                private Long municipioId;
                private String municipioName;
                private Long provinciaId;
                private String provinciaName;

                public Location() {}

                public Location(Long localidadId, String localidadName,
                                Long municipioId, String municipioName,
                                Long provinciaId, String provinciaName) {
                        this.localidadId = localidadId;
                        this.localidadName = localidadName;
                        this.municipioId = municipioId;
                        this.municipioName = municipioName;
                        this.provinciaId = provinciaId;
                        this.provinciaName = provinciaName;
                }

                public boolean hasCompleteAddress() {
                        return localidadName != null && municipioName != null && provinciaName != null;
                }

                public String getFullAddress() {
                        if (!hasCompleteAddress()) {
                                return "";
                        }
                        return localidadName + ", " + municipioName + ", " + provinciaName;
                }

                // Getters and Setters
                public Long getLocalidadId() { return localidadId; }
                public void setLocalidadId(Long localidadId) { this.localidadId = localidadId; }
                public String getLocalidadName() { return localidadName; }
                public void setLocalidadName(String localidadName) { this.localidadName = localidadName; }
                public Long getMunicipioId() { return municipioId; }
                public void setMunicipioId(Long municipioId) { this.municipioId = municipioId; }
                public String getMunicipioName() { return municipioName; }
                public void setMunicipioName(String municipioName) { this.municipioName = municipioName; }
                public Long getProvinciaId() { return provinciaId; }
                public void setProvinciaId(Long provinciaId) { this.provinciaId = provinciaId; }
                public String getProvinciaName() { return provinciaName; }
                public void setProvinciaName(String provinciaName) { this.provinciaName = provinciaName; }
        }
}
