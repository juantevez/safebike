package com.safe.bike.infrastructure.web;

import com.safe.bike.domain.model.dto.BikeModelDto;
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
import java.util.Objects;

@Route("bike-form")
public class BikeFormView extends VerticalLayout {

    private static final Logger logger = LoggerFactory.getLogger(BikeFormView.class);

    private final BikeServicePort bikeService;
    private final BrandServicePort brandService;
    private final BikeTypeServicePort bikeTypeService;
    private final UserServicePort userService;
    private final BikeModelServicePort bikeModelServicePort;
    private final MonedaServicePort monedaServicePort;
    private final JwtUtil jwtUtil;

    private ComboBox<BrandEntity> brandComboBox = new ComboBox<>("Marca");
    private ComboBox<BikeTypeEntity> bikeTypeComboBox = new ComboBox<>("Tipo de Bicicleta");
    private ComboBox<BikeModelDto> bikeModelComboBox = new ComboBox<>("Modelo"); // ✅ Cambiado a DTO

    private TextField serialNumberField = new TextField("Número de Serie");
    private DatePicker purchaseDateField = new DatePicker("Fecha de Compra");

    private ComboBox<MonedaEntity> monedaComboBox = new ComboBox<>("Moneda");
    private NumberField purchaseValueField = new NumberField("Valor de Compra");

    private Button saveButton = new Button("Guardar Bicicleta");

    // Binder ahora usa BikeEntity, pero manejamos BikeModelDto en el modelo
    private Binder<BikeEntity> binder = new Binder<>(BikeEntity.class);

    private User currentUser;
    private UserEntity currentUserEntity;

    // Cache temporal para mapear DTO → Entity al guardar
    private final java.util.Map<BikeModelDto, BikeModelEntity> dtoToEntityMap = new java.util.HashMap<>();

    @Autowired
    public BikeFormView(
            BikeServicePort bikeService,
            BrandServicePort brandService,
            BikeTypeServicePort bikeTypeService,
            UserServicePort userService,
            BikeModelServicePort bikeModelServicePort,
            MonedaServicePort monedaServicePort, JwtUtil jwtUtil) {

        logger.info("Inicializando BikeFormView");

        this.bikeService = bikeService;
        this.brandService = brandService;
        this.bikeTypeService = bikeTypeService;
        this.userService = userService;
        this.bikeModelServicePort = bikeModelServicePort;
        this.monedaServicePort = monedaServicePort;
        this.jwtUtil = jwtUtil;

        getCurrentUser();
        configureComboBoxes();
        configureBinder();
        saveButton.addClickListener(event -> saveBike());

        FormLayout formLayout = new FormLayout();
        formLayout.add(
                brandComboBox,
                bikeTypeComboBox,
                bikeModelComboBox,
                serialNumberField,
                purchaseDateField,
                monedaComboBox,
                purchaseValueField
        );

        add(formLayout, saveButton);
        setAlignItems(Alignment.CENTER);

        logger.info("BikeFormView inicializado correctamente");
    }

    private void configureBinder() {
        binder.forField(brandComboBox)
                .asRequired("La marca es obligatoria")
                .bind(BikeEntity::getBrand, BikeEntity::setBrand);

        binder.forField(bikeTypeComboBox)
                .asRequired("El tipo de bicicleta es obligatorio")
                .bind(BikeEntity::getBikeType, BikeEntity::setBikeType);

        // No bindeamos directamente el DTO, lo manejamos manualmente
        binder.forField(serialNumberField)
                .asRequired("El número de serie es obligatorio")
                .bind(BikeEntity::getSerialNumber, BikeEntity::setSerialNumber);

        binder.forField(purchaseDateField)
                .withValidator(purchaseDate -> {
                    if (purchaseDate == null) return true;
                    return !purchaseDate.isAfter(LocalDate.now());
                }, "La fecha de compra debe ser igual o anterior a la fecha actual")
                .bind(BikeEntity::getPurchaseDate, BikeEntity::setPurchaseDate);

        binder.forField(monedaComboBox)
                        .bind(BikeEntity::getMoneda, BikeEntity::setMoneda);

        binder.forField(purchaseValueField)
                .bind(BikeEntity::getPurchaseValue, BikeEntity::setPurchaseValue);
    }

    private void getCurrentUser() {
        logger.info("=== DEBUGGING getCurrentUser() ===");

        try {
            String authToken = (String) VaadinSession.getCurrent().getAttribute("authToken");
            String userEmail = (String) VaadinSession.getCurrent().getAttribute("userEmail");

            logger.info("Token en sesión: {}", authToken != null ? "presente" : "ausente");
            logger.info("Email en sesión: {}", userEmail);

            if (authToken != null && userEmail != null && jwtUtil.isTokenValid(authToken, userEmail)) {
                this.currentUser = userService.findByEmail(userEmail);
                if (this.currentUser != null) {
                    logger.info("Usuario encontrado: ID={}, Email={}", currentUser.getId(), currentUser.getEmail());
                    this.currentUserEntity = convertUserToUserEntity(this.currentUser);
                    return;
                } else {
                    logger.warn("Usuario no encontrado en la base de datos con email: {}", userEmail);
                }
            }

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && !"anonymousUser".equals(authentication.getName())) {
                String springUserEmail = authentication.getName();
                this.currentUser = userService.findByEmail(springUserEmail);
                if (this.currentUser != null) {
                    this.currentUserEntity = convertUserToUserEntity(this.currentUser);
                    return;
                }
            }

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
            // Cargar marcas
            List<BrandEntity> brands = brandService.getAllBrands();
            if (brands != null && !brands.isEmpty()) {
                brandComboBox.setItems(brands);
                brandComboBox.setItemLabelGenerator(brand -> Objects.toString(brand.getName(), "Sin nombre"));
                logger.info("Marcas cargadas: {}", brands.size());
            } else {
                logger.warn("No hay marcas disponibles");
                Notification.show("No hay marcas disponibles", 3000, Notification.Position.MIDDLE);
            }

            // Cargar tipos
            List<BikeTypeEntity> bikeTypes = bikeTypeService.getAllBikeTypes();
            if (bikeTypes != null && !bikeTypes.isEmpty()) {
                bikeTypeComboBox.setItems(bikeTypes);
                bikeTypeComboBox.setItemLabelGenerator(type -> Objects.toString(type.getName(), "Sin tipo"));
                logger.info("Tipos de bicicleta cargados: {}", bikeTypes.size());
            } else {
                logger.warn("No hay tipos de bicicleta disponibles");
                Notification.show("No hay tipos disponibles", 3000, Notification.Position.MIDDLE);
            }

            // Cargar entidades con detalles para mapeo interno
            List<BikeModelEntity> allModelEntities = bikeModelServicePort.findAllWithDetails();
            if (allModelEntities != null && !allModelEntities.isEmpty()) {
                dtoToEntityMap.clear();

                for (BikeModelEntity entity : allModelEntities) {
                    BikeModelDto dto = new BikeModelDto(
                            entity.getIdBikeModel(),  // Ya es Long
                            entity.getModelName(),
                            entity.getBrand().getBrandId(),
                            entity.getBrand().getName(),
                            entity.getBikeType().getBikeTypeId(),  // Ya es Long
                            entity.getBikeType().getName()
                    );
                    dtoToEntityMap.put(dto, entity);
                }

                logger.info("Mapeo DTO → Entity creado para {} modelos", dtoToEntityMap.size());
            }

            // Cargar monedas
            List<MonedaEntity> monedas = monedaServicePort.findAllMonedas();
            if (monedas != null && !monedas.isEmpty()) {
                monedaComboBox.setItems(monedas);
                monedaComboBox.setItemLabelGenerator(moneda -> Objects.toString(moneda.getCodigoMoneda(), "Sin nombre"));
                logger.info("Marcas cargadas: {}", monedas.size());
            } else {
                logger.warn("No hay marcas disponibles");
                Notification.show("No hay marcas disponibles", 3000, Notification.Position.MIDDLE);
            }
            configureDynamicModelFiltering();

        } catch (Exception e) {
            logger.error("Error al configurar ComboBoxes", e);
            Notification.show("Error al cargar datos: " + e.getMessage(),
                    5000, Notification.Position.MIDDLE);
        }
    }

    private void configureDynamicModelFiltering() {
        Runnable updateModels = () -> {
            BrandEntity selectedBrand = brandComboBox.getValue();
            BikeTypeEntity selectedType = bikeTypeComboBox.getValue();

            bikeModelComboBox.clear();

            if (selectedBrand != null && selectedType != null) {
                logger.info("Filtrando modelos por marca: {} y tipo: {}", selectedBrand.getName(), selectedType.getName());

                List<BikeModelDto> filtered = bikeModelServicePort.getModelsByBrandAndType(
                        selectedBrand.getBrandId().longValue(),
                        selectedType.getBikeTypeId().longValue()
                );

                if (filtered != null && !filtered.isEmpty()) {
                    bikeModelComboBox.setItems(filtered);
                    bikeModelComboBox.setItemLabelGenerator(BikeModelDto::modelName);
                    bikeModelComboBox.setEnabled(true);
                    bikeModelComboBox.setPlaceholder("Seleccione un modelo");
                    logger.info("Modelos filtrados: {} encontrados", filtered.size());
                } else {
                    bikeModelComboBox.setEnabled(false);
                    bikeModelComboBox.setPlaceholder("No hay modelos para esta combinación");
                    logger.info("No hay modelos para la combinación seleccionada");
                }
            } else if (selectedBrand != null) {
                logger.info("Filtrando solo por marca: {}", selectedBrand.getName());
                List<BikeModelDto> filtered = bikeModelServicePort.getModelsByBrand(selectedBrand.getBrandId().longValue());
                setModelsInComboBox(filtered, "No hay modelos para esta marca");
            } else if (selectedType != null) {
                logger.info("Filtrando solo por tipo: {}", selectedType.getName());
                List<BikeModelDto> filtered = bikeModelServicePort.getModelsByType(selectedType.getBikeTypeId().longValue());
                setModelsInComboBox(filtered, "No hay modelos para este tipo");
            } else {
                bikeModelComboBox.setEnabled(false);
                bikeModelComboBox.setPlaceholder("Seleccione marca y tipo");
            }
        };

        brandComboBox.addValueChangeListener(event -> updateModels.run());
        bikeTypeComboBox.addValueChangeListener(event -> updateModels.run());

        bikeModelComboBox.setEnabled(false);
        bikeModelComboBox.setPlaceholder("Seleccione marca y tipo");
    }

    private void setModelsInComboBox(List<BikeModelDto> models, String placeholder) {
        if (models != null && !models.isEmpty()) {
            bikeModelComboBox.setItems(models);
            bikeModelComboBox.setItemLabelGenerator(BikeModelDto::modelName);
            bikeModelComboBox.setEnabled(true);
            bikeModelComboBox.setPlaceholder("Seleccione un modelo");
        } else {
            bikeModelComboBox.setEnabled(false);
            bikeModelComboBox.setPlaceholder(placeholder);
        }
    }

    private void saveBike() {
        logger.info("Intentando guardar bicicleta");

        if (currentUser == null || currentUserEntity == null) {
            logger.error("Usuario no identificado");
            Notification.show("Por favor, inicie sesión nuevamente.", 5000, Notification.Position.MIDDLE);
            UI.getCurrent().navigate("login");
            return;
        }

        BikeEntity bike = new BikeEntity();
        if (binder.writeBeanIfValid(bike)) {
            try {
                // Asignar usuario
                bike.setUser(currentUserEntity);

                // Mapear BikeModelDto → BikeModelEntity si fue seleccionado
                BikeModelDto selectedDto = bikeModelComboBox.getValue();
                if (selectedDto != null) {
                    BikeModelEntity modelEntity = dtoToEntityMap.get(selectedDto);
                    if (modelEntity != null) {
                        bike.setBikeModel(modelEntity);
                    } else {
                        logger.warn("No se encontró BikeModelEntity para el DTO seleccionado: {}", selectedDto.modelName());
                        Notification.show("Error: modelo no válido", 3000, Notification.Position.MIDDLE);
                        return;
                    }
                }

                logger.info("Guardando bicicleta: marca={}, tipo={}, modelo={}, serial={}",
                        bike.getBrand().getName(),
                        bike.getBikeType().getName(),
                        bike.getBikeModel() != null ? bike.getBikeModel().getModelName() : "N/A",
                        bike.getSerialNumber());

                bikeService.save(bike);
                logger.info("Bicicleta guardada exitosamente: {}", bike.getBikeId());

                Notification.show("✅ Bicicleta guardada exitosamente!", 3000, Notification.Position.MIDDLE);

                // Limpiar formulario
                binder.readBean(new BikeEntity());
                brandComboBox.clear();
                bikeTypeComboBox.clear();
                bikeModelComboBox.clear();

            } catch (Exception e) {
                logger.error("Error al guardar bicicleta", e);
                Notification.show("❌ Error al guardar: " + e.getMessage(), 5000, Notification.Position.MIDDLE);
            }
        } else {
            logger.warn("Formulario inválido");
            binder.validate();
            Notification.show("Por favor complete los campos obligatorios", 3000, Notification.Position.MIDDLE);
        }
    }
}