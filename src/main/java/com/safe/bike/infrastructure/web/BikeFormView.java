package com.safe.bike.infrastructure.web;

import com.safe.bike.domain.model.dto.BikeModelDto;
import com.safe.bike.domain.model.entity.*;
import com.safe.bike.domain.port.in.*;
import com.safe.user.adapter.out.persistence.entity.UserEntity;
import com.safe.user.application.service.AuthService;
import com.safe.user.config.JwtUtil;
import com.safe.user.infrastructure.port.UserServicePort;
import com.safe.user.model.User;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
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
    private final AuthService authService; // Agregar AuthService

    private ComboBox<BrandEntity> brandComboBox = new ComboBox<>("Marca");
    private ComboBox<BikeTypeEntity> bikeTypeComboBox = new ComboBox<>("Tipo de Bicicleta");
    private ComboBox<BikeModelDto> bikeModelComboBox = new ComboBox<>("Modelo");

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
            MonedaServicePort monedaServicePort,
            JwtUtil jwtUtil,
            AuthService authService) { // Agregar AuthService al constructor

        logger.info("Inicializando BikeFormView");

        this.bikeService = bikeService;
        this.brandService = brandService;
        this.bikeTypeService = bikeTypeService;
        this.userService = userService;
        this.bikeModelServicePort = bikeModelServicePort;
        this.monedaServicePort = monedaServicePort;
        this.jwtUtil = jwtUtil;
        this.authService = authService; // Asignar AuthService

        // TEMPORAL: Comentar para debugging
        // clearInvalidTokens();

        getCurrentUser();

        // DEBUGGING: Estado inicial del usuario
        logger.info("=== ESTADO INICIAL DEL USUARIO ===");
        logger.info("CurrentUser: {}", currentUser != null ? currentUser.getEmail() : "NULL");
        logger.info("CurrentUserEntity: {}", currentUserEntity != null ? currentUserEntity.getEmail() : "NULL");
        logger.info("================================");

        // Crear header con logout
        HorizontalLayout header = createHeader();

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

        // Agregar header al inicio
        add(header, formLayout, saveButton);
        setAlignItems(Alignment.CENTER);

        logger.info("BikeFormView inicializado correctamente");
    }

    /**
     * MÉTODO TEMPORAL: Limpiar tokens incompatibles después del cambio de algoritmo
     */
    private void clearInvalidTokens() {
        try {
            String authToken = (String) VaadinSession.getCurrent().getAttribute("authToken");
            String userEmail = (String) VaadinSession.getCurrent().getAttribute("userEmail");

            if (authToken != null && userEmail != null) {
                // Intentar validar el token existente
                if (!jwtUtil.validateToken(authToken, userEmail)) {
                    logger.warn("Token incompatible detectado, limpiando sesión...");
                    VaadinSession.getCurrent().setAttribute("authToken", null);
                    VaadinSession.getCurrent().setAttribute("userEmail", null);

                    Notification.show("⚠️ Sesión expirada por cambios de seguridad. Por favor, inicie sesión nuevamente.",
                            5000, Notification.Position.MIDDLE);
                    UI.getCurrent().navigate("login");
                    return;
                }
            }
        } catch (Exception e) {
            logger.warn("Error verificando token, limpiando sesión: {}", e.getMessage());
            VaadinSession.getCurrent().setAttribute("authToken", null);
            VaadinSession.getCurrent().setAttribute("userEmail", null);
            UI.getCurrent().navigate("login");
        }
    }

    /**
     * Verificar y revalidar el estado del usuario actual
     */
    private boolean ensureUserIsValid() {
        logger.info("=== DEBUGGING ensureUserIsValid() ===");

        try {
            // Verificar sesión actual
            String authToken = (String) VaadinSession.getCurrent().getAttribute("authToken");
            String userEmail = (String) VaadinSession.getCurrent().getAttribute("userEmail");

            logger.info("Estado inicial - Token: {}, Email: {}, CurrentUser: {}",
                    authToken != null ? "presente" : "AUSENTE",
                    userEmail != null ? userEmail : "AUSENTE",
                    currentUser != null ? currentUser.getEmail() : "AUSENTE");

            // Si hay token y email, validar
            if (authToken != null && userEmail != null) {
                logger.info("Intentando validar token para usuario: {}", userEmail);

                if (jwtUtil.validateToken(authToken, userEmail)) {
                    logger.info("✅ Token VÁLIDO para usuario: {}", userEmail);

                    // Token válido, verificar si tenemos el usuario cargado
                    if (currentUser == null || !currentUser.getEmail().equals(userEmail)) {
                        logger.info("Recargando usuario desde sesión válida");
                        getCurrentUser();
                    }

                    boolean isValid = currentUser != null && currentUserEntity != null;
                    logger.info("Usuario final válido: {}", isValid);
                    return isValid;
                } else {
                    logger.warn("❌ Token INVÁLIDO o expirado para usuario: {}", userEmail);
                    return false;
                }
            } else {
                logger.warn("❌ No hay token o email en la sesión");
            }

            // Fallback con Spring Security
            logger.info("Intentando fallback con Spring Security...");
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            logger.info("Spring Authentication - Existe: {}, Nombre: {}, Autenticado: {}",
                    authentication != null,
                    authentication != null ? authentication.getName() : "null",
                    authentication != null ? authentication.isAuthenticated() : false);

            if (authentication != null &&
                    authentication.isAuthenticated() &&
                    !"anonymousUser".equals(authentication.getName())) {

                String springEmail = authentication.getName();
                logger.info("✅ Encontrado usuario en Spring Security: {}", springEmail);

                if (currentUser == null || !currentUser.getEmail().equals(springEmail)) {
                    logger.info("Recargando usuario desde Spring Security");
                    getCurrentUser();
                }

                boolean isValid = currentUser != null && currentUserEntity != null;
                logger.info("Usuario final válido desde Spring: {}", isValid);
                return isValid;
            } else {
                logger.warn("❌ No hay autenticación válida en Spring Security");
            }

            logger.error("❌ No se pudo obtener usuario por ningún método");
            return false;

        } catch (Exception e) {
            logger.error("💥 ERROR en ensureUserIsValid(): {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Crear el header con título y botón de logout
     */
    private HorizontalLayout createHeader() {
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.getStyle().set("padding", "10px 20px");
        header.getStyle().set("background-color", "#f5f5f5");
        header.getStyle().set("border-bottom", "1px solid #ddd");
        header.getStyle().set("margin-bottom", "20px");

        // Título y información del usuario
        Div leftSection = new Div();
        H3 title = new H3("Registro de Bicicleta");
        title.getStyle().set("margin", "0");
        title.getStyle().set("color", "#2c3e50");

        Span userInfo = new Span();
        if (currentUser != null) {
            userInfo.setText("Bienvenido: " + currentUser.getFirstName() + " " + currentUser.getLastName());
            userInfo.getStyle().set("color", "#7f8c8d");
            userInfo.getStyle().set("font-size", "14px");
        }

        leftSection.add(title, userInfo);

        // Botón de logout con icono
        Button logoutButton = new Button();
        logoutButton.setIcon(new Icon(VaadinIcon.SIGN_OUT));
        logoutButton.setText("Cerrar Sesión");
        logoutButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        logoutButton.getStyle().set("color", "#e74c3c");
        logoutButton.getStyle().set("cursor", "pointer");

        // Tooltip para el botón
        logoutButton.getElement().setAttribute("title", "Cerrar Sesión");

        // Evento de click para logout
        logoutButton.addClickListener(event -> performLogout());

        header.add(leftSection, logoutButton);
        return header;
    }

    /**
     * Realizar logout
     */
    private void performLogout() {
        logger.info("Iniciando proceso de logout para usuario: {}",
                currentUser != null ? currentUser.getEmail() : "desconocido");

        try {
            // Obtener token de la sesión
            String authToken = (String) VaadinSession.getCurrent().getAttribute("authToken");

            if (authToken != null) {
                // Llamar al servicio de logout
                authService.logout(authToken);
                logger.info("Logout procesado correctamente");
            }

            // Limpiar sesión de Vaadin
            VaadinSession.getCurrent().setAttribute("authToken", null);
            VaadinSession.getCurrent().setAttribute("userEmail", null);

            // Limpiar contexto de seguridad de Spring
            SecurityContextHolder.clearContext();

            // Cerrar sesión de Vaadin
            VaadinSession.getCurrent().close();

            // Mostrar notificación y redirigir
            Notification.show("✅ Sesión cerrada correctamente", 2000, Notification.Position.TOP_CENTER);

            // Redirigir al login
            UI.getCurrent().navigate("login");

        } catch (Exception e) {
            logger.error("Error durante logout: {}", e.getMessage(), e);

            // Aunque haya error, limpiar sesión local
            VaadinSession.getCurrent().setAttribute("authToken", null);
            VaadinSession.getCurrent().setAttribute("userEmail", null);
            SecurityContextHolder.clearContext();
            VaadinSession.getCurrent().close();

            Notification.show("⚠️ Sesión cerrada (con advertencias)", 3000, Notification.Position.TOP_CENTER);
            UI.getCurrent().navigate("login");
        }
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

            logger.info("Sesión - Token: {}, Email: {}",
                    authToken != null ? "presente (" + authToken.substring(0, Math.min(20, authToken.length())) + "...)" : "AUSENTE",
                    userEmail);

            if (authToken != null && userEmail != null) {
                logger.info("Validando token para email: {}", userEmail);

                if (jwtUtil.validateToken(authToken, userEmail)) {
                    logger.info("✅ Token válido, buscando usuario en BD...");
                    this.currentUser = userService.findByEmail(userEmail);

                    if (this.currentUser != null) {
                        logger.info("✅ Usuario encontrado por sesión: ID={}, Email={}, Nombre={}",
                                currentUser.getId(), currentUser.getEmail(),
                                currentUser.getFirstName() + " " + currentUser.getLastName());
                        this.currentUserEntity = convertUserToUserEntity(this.currentUser);
                        logger.info("✅ UserEntity creado correctamente");
                        return;
                    } else {
                        logger.error("❌ Usuario NO encontrado en la base de datos con email: {}", userEmail);
                    }
                } else {
                    logger.warn("❌ Token inválido para email: {}", userEmail);
                }
            } else {
                logger.warn("❌ Token o email ausente en sesión - Token: {}, Email: {}",
                        authToken != null, userEmail != null);
            }

            // Fallback: intentar con Spring Security
            logger.info("Intentando fallback con Spring Security...");
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            logger.info("Spring Security - Auth: {}, Principal: {}, Autenticado: {}",
                    authentication != null,
                    authentication != null ? authentication.getName() : "null",
                    authentication != null ? authentication.isAuthenticated() : false);

            if (authentication != null &&
                    authentication.isAuthenticated() &&
                    !"anonymousUser".equals(authentication.getName())) {

                String springUserEmail = authentication.getName();
                logger.info("Intentando cargar usuario desde Spring Security: {}", springUserEmail);

                this.currentUser = userService.findByEmail(springUserEmail);
                if (this.currentUser != null) {
                    logger.info("✅ Usuario encontrado por Spring Security: ID={}, Email={}",
                            currentUser.getId(), currentUser.getEmail());
                    this.currentUserEntity = convertUserToUserEntity(this.currentUser);

                    // Actualizar sesión con datos correctos
                    VaadinSession.getCurrent().setAttribute("userEmail", springUserEmail);
                    logger.info("✅ Sesión actualizada con email de Spring Security");
                    return;
                } else {
                    logger.error("❌ Usuario NO encontrado en BD con email de Spring Security: {}", springUserEmail);
                }
            } else {
                logger.warn("❌ No hay autenticación válida en Spring Security");
            }

            // Si llegamos aquí, no se pudo obtener el usuario
            logger.error("💥 FALLO TOTAL: No se pudo obtener el usuario por ningún método");
            this.currentUser = null;
            this.currentUserEntity = null;

        } catch (Exception e) {
            logger.error("💥 ERROR GRAVE en getCurrentUser()", e);
            this.currentUser = null;
            this.currentUserEntity = null;
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
                monedaComboBox.setWidth("75px");
                monedaComboBox.getStyle().set("font-family", "monospace");
                logger.info("Monedas cargadas: {}", monedas.size());
            } else {
                logger.warn("No hay monedas disponibles");
                Notification.show("No hay monedas disponibles", 3000, Notification.Position.MIDDLE);
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

        // Debug: verificar estado del usuario antes de guardar
        logger.debug("Estado del usuario - currentUser: {}, currentUserEntity: {}",
                currentUser != null ? currentUser.getEmail() : "null",
                currentUserEntity != null ? currentUserEntity.getEmail() : "null");

        // Si el usuario es null, intentar recargar usando método mejorado
        if (!ensureUserIsValid()) {
            logger.error("No se pudo validar o recargar el usuario");
            Notification.show("Sesión expirada. Por favor, inicie sesión nuevamente.",
                    5000, Notification.Position.MIDDLE);
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