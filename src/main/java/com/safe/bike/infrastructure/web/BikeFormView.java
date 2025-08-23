package com.safe.bike.infrastructure.web;

import com.safe.bike.domain.model.entity.*;
import com.safe.bike.domain.port.in.*;
import com.safe.user.adapter.out.persistence.entity.UserEntity;
import com.safe.user.config.JwtUtil;
import com.safe.user.infrastructure.port.UserServicePort;
import com.safe.user.model.User;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.List;

@Route("bike-form")
public class BikeFormView extends VerticalLayout {
    private static final Logger logger = LoggerFactory.getLogger(BikeFormView.class);

    private final BikeServicePort bikeService;
    private final BrandServicePort brandService;
    private final UserServicePort userService;
    private final BikeModelServicePort bikeModelServicePort;
    private JwtUtil jwtUtil;

    // ✅ REMOVIDO: userComboBox ya no está en el formulario
    private ComboBox<BrandEntity> brandComboBox = new ComboBox<>("Marca");
    private ComboBox<BikeModelEntity> modelComboBox = new ComboBox<>("Modelo");
    private TextField serialNumberField = new TextField("Número de Serie");
    private DatePicker purchaseDateField = new DatePicker("Fecha de Compra");
    private NumberField purchaseValueField = new NumberField("Valor de Compra");

    // Botón de guardado
    private Button saveButton = new Button("Guardar Bicicleta");

    // Binder para enlazar los campos a la entidad
    private Binder<BikeEntity> binder = new Binder<>(BikeEntity.class);

    // ✅ NUEVO: Variable para almacenar el usuario logueado
    private User currentUser;
    private UserEntity currentUserEntity;

    @Autowired
    public BikeFormView(
            BikeServicePort bikeService,
            BrandServicePort brandService,
            UserServicePort userService,
            BikeModelServicePort bikeModelServicePort, JwtUtil jwtUtil) {

        logger.info("Inicializando BikeFormView");

        this.bikeService = bikeService;
        this.brandService = brandService;
        this.bikeModelServicePort = bikeModelServicePort;
        this.userService = userService;
        this.jwtUtil = jwtUtil;

        // ✅ NUEVO: Obtener el usuario logueado
        getCurrentUser();

        // Configurar los ComboBox con los datos de la base de datos
        configureComboBoxes();

        // ✅ ACTUALIZADO: Configurar el Binder sin userComboBox
        binder.forField(brandComboBox).bind(BikeEntity::getBrand, BikeEntity::setBrand);
        binder.forField(serialNumberField).bind(BikeEntity::getSerialNumber, BikeEntity::setSerialNumber);
        binder.forField(modelComboBox).bind(BikeEntity::getBikeModel, BikeEntity::setBikeModel);
        binder.forField(purchaseDateField)
                .withValidator(purchaseDate -> {
                    if (purchaseDate == null) return true; // Permitir fecha vacía si no es obligatoria
                    return !purchaseDate.isAfter(LocalDate.now());
                }, "La fecha de compra debe ser igual o anterior a la fecha actual")
                .bind(BikeEntity::getPurchaseDate, BikeEntity::setPurchaseDate);
        binder.forField(purchaseValueField).bind(BikeEntity::getPurchaseValue, BikeEntity::setPurchaseValue);

        // Configurar el botón de guardar
        saveButton.addClickListener(event -> saveBike());

        // ✅ ACTUALIZADO: Layout del formulario sin userComboBox
        FormLayout formLayout = new FormLayout();
        formLayout.add(brandComboBox, modelComboBox, serialNumberField,
                purchaseDateField, purchaseValueField, saveButton);

        add(formLayout);
        setAlignItems(Alignment.CENTER);

        logger.info("BikeFormView inicializado correctamente");
    }

    // ✅ NUEVO: Método para obtener el usuario logueado usando JWT
    private void getCurrentUser() {
        logger.info("=== DEBUGGING getCurrentUser() ===");

        try {
            // Opción 1: Obtener desde el token JWT en la sesión
            String authToken = (String) VaadinSession.getCurrent().getAttribute("authToken");
            String userEmail = (String) VaadinSession.getCurrent().getAttribute("userEmail");

            logger.info("Token en sesión: {}", authToken != null ? "presente" : "ausente");
            logger.info("Email en sesión: {}", userEmail);

            if (authToken != null && userEmail != null) {
                // Validar que el token sigue siendo válido
                if (jwtUtil.isTokenValid(authToken, userEmail)) {
                    logger.info("Token JWT válido para email: {}", userEmail);

                    // Buscar el usuario en la base de datos
                    this.currentUser = userService.findByEmail(userEmail);
                    if (this.currentUser != null) {
                        logger.info("Usuario encontrado: ID={}, Email={}", currentUser.getId(), currentUser.getEmail());
                        this.currentUserEntity = convertUserToUserEntity(this.currentUser);
                        return; // Usuario encontrado exitosamente
                    } else {
                        logger.warn("Usuario no encontrado en la base de datos con email: {}", userEmail);
                    }
                } else {
                    logger.warn("Token JWT inválido o expirado para email: {}", userEmail);
                    // Limpiar sesión con token inválido
                    VaadinSession.getCurrent().setAttribute("authToken", null);
                    VaadinSession.getCurrent().setAttribute("userEmail", null);
                }
            } else {
                logger.warn("No hay token JWT o email en la sesión");
            }

            // Opción 2: Fallback - obtener desde Spring Security (poco probable con JWT)
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && !"anonymousUser".equals(authentication.getName())) {
                logger.info("Intentando obtener usuario desde Spring Security Context");
                String springUserEmail = authentication.getName();
                this.currentUser = userService.findByEmail(springUserEmail);
                if (this.currentUser != null) {
                    this.currentUserEntity = convertUserToUserEntity(this.currentUser);
                    return;
                }
            }

            // Si no se pudo obtener el usuario, redirigir al login
            if (this.currentUser == null) {
                logger.error("No se pudo obtener el usuario logueado");
                Notification.show("Sesión expirada o inválida. Debe iniciar sesión nuevamente.",
                        5000, Notification.Position.MIDDLE);
                UI.getCurrent().navigate("login");
            }

        } catch (Exception e) {
            logger.error("Error al obtener usuario logueado", e);
            Notification.show("Error al obtener usuario: " + e.getMessage(),
                    5000, Notification.Position.MIDDLE);
            UI.getCurrent().navigate("login");
        }
    }

    // ✅ NUEVO: Método para convertir User (dominio) a UserEntity (JPA)
    private UserEntity convertUserToUserEntity(User user) {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(user.getId());
        userEntity.setEmail(user.getEmail());
        userEntity.setUsername(user.getUsername());
        userEntity.setFirstName(user.getFirstName());
        userEntity.setLastName(user.getLastName());
        userEntity.setPassword(user.getPassword());
        userEntity.setRole(user.getRole());
        userEntity.setCreatedAt(user.getCreatedAt());
        return userEntity;
    }

    private void configureComboBoxes() {
        logger.info("Configurando ComboBoxes del formulario");

        try {
            // ✅ REMOVIDO: Código para cargar usuarios ya no es necesario

            // Cargar las marcas desde el servicio
            logger.info("Cargando marcas para ComboBox");
            List<BrandEntity> brands = brandService.getAllBrands();
            brandComboBox.setItems(brands);
            brandComboBox.setItemLabelGenerator(brand -> {
                String name = brand.getName();
                if (name == null || name.trim().isEmpty()) {
                    logger.warn("Marca encontrada con nombre null o vacío: {}", brand);
                    return "Sin nombre";
                }
                return name;
            });
            logger.info("ComboBox de marcas configurado con {} elementos", brands.size());

            brandComboBox.addValueChangeListener(event -> {
                if (event.getValue() != null) {
                    List<BikeModelEntity> models = bikeModelServicePort.getBikeModelsByBrandId(event.getValue().getBrandId());
                    modelComboBox.setItems(models);
                    modelComboBox.setItemLabelGenerator(BikeModelEntity::getModelName);
                    modelComboBox.setEnabled(true);
                } else {
                    modelComboBox.clear();
                    modelComboBox.setEnabled(false);
                }
            });

        } catch (Exception e) {
            logger.error("Error al configurar ComboBoxes", e);
            Notification.show("Error al cargar los datos del formulario: " + e.getMessage(),
                    5000, Notification.Position.MIDDLE);
        }
    }

    private void saveBike() {
        logger.info("Intentando guardar bicicleta");

        // ✅ VALIDACIÓN: Verificar que tenemos un usuario logueado
        if (currentUser == null || currentUserEntity == null) {
            logger.error("No se puede guardar la bicicleta: usuario no identificado");
            Notification.show("Error: Usuario no identificado. Por favor, inicie sesión nuevamente.",
                    5000, Notification.Position.MIDDLE);
            return;
        }

        BikeEntity bike = new BikeEntity();
        if (binder.writeBeanIfValid(bike)) {
            try {
                // ✅ CORREGIDO: Asignar el UserEntity (no User) a la bicicleta
                bike.setUser(currentUserEntity);

                logger.debug("Datos de la bicicleta a guardar: {}", bike);
                logger.info("Usuario asignado: ID={}, Email={}", currentUser.getId(), currentUser.getEmail());

                bikeService.save(bike);
                logger.info("Bicicleta guardada exitosamente");

                Notification.show("Bicicleta guardada exitosamente!", 3000, Notification.Position.MIDDLE);
                binder.readBean(null); // Limpiar el formulario

            } catch (Exception e) {
                logger.error("Error al guardar bicicleta: {}", bike, e);
                Notification.show("Error al guardar: " + e.getMessage(), 5000, Notification.Position.MIDDLE);
            }
        } else {
            logger.warn("Formulario inválido - no se puede guardar la bicicleta");
            Notification.show("Hay errores en el formulario. Por favor, corrígelos.", 3000, Notification.Position.MIDDLE);
        }
    }
}