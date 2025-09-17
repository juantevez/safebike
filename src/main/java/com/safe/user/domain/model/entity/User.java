package com.safe.user.domain.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "users",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_user_email", columnNames = "email"),
                @UniqueConstraint(name = "uk_user_username", columnNames = "username")
        })
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @Column(name = "role", nullable = false)
    private String role = "USER"; // valor por defecto

    @UpdateTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // ✅ RELACIONES GEOGRÁFICAS
    @Column(name = "provincia_id")
    private Integer provinciaId;

    @Column(name = "municipio_id")
    private Integer municipioId;

    @Column(name = "localidad_id")
    private Integer localidadId;

    // ✅ NOMBRES GEOGRÁFICOS (campos transient - no se guardan en BD)
    @Transient
    private String localidadNombre;

    @Transient
    private String municipioNombre;

    @Transient
    private String provinciaNombre;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // ✅ CONSTRUCTOR CON PARÁMETROS BÁSICOS
    public User(String username, String email, String password, String firstName, String lastName) {
        this();
        this.username = username;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = "USER";
    }

    // ✅ CONSTRUCTOR COMPLETO
    public User(Long id, String username, String email, String password, String firstName, String lastName, LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.createdAt = createdAt;
        this.role = "USER";
    }

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // ✅ MÉTODOS DE NEGOCIO
    public String getFullName() {
        return firstName + " " + lastName;
    }

    public String getUbicacionCompleta() {
        StringBuilder ubicacion = new StringBuilder();
        if (provinciaNombre != null) {
            ubicacion.append(provinciaNombre);
            if (municipioNombre != null) {
                ubicacion.append(" > ").append(municipioNombre);
                if (localidadNombre != null) {
                    ubicacion.append(" > ").append(localidadNombre);
                }
            }
        }
        return ubicacion.toString();
    }

    // ✅ GETTERS Y SETTERS
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public Integer getLocalidadId() {
        return localidadId;
    }

    public void setLocalidadId(Integer localidad) {
        this.localidadId = localidad;
    }

    public Integer getMunicipioId() {
        return municipioId;
    }

    public void setMunicipioId(Integer municipio) {
        this.municipioId = municipio;
    }

    public Integer getProvinciaId() {
        return provinciaId;
    }

    public void setProvinciaId(Integer provincia) {
        this.provinciaId = provincia;
    }

    public String getLocalidadNombre() { return localidadNombre; }
    public void setLocalidadNombre(String localidadNombre) { this.localidadNombre = localidadNombre; }

    public String getMunicipioNombre() { return municipioNombre; }
    public void setMunicipioNombre(String municipioNombre) { this.municipioNombre = municipioNombre; }

    public String getProvinciaNombre() { return provinciaNombre; }
    public void setProvinciaNombre(String provinciaNombre) { this.provinciaNombre = provinciaNombre; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // ✅ MÉTODOS ESTÁNDAR
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", username='" + username + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}