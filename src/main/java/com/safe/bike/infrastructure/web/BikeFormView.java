package com.safe.bike.infrastructure.web;

import com.safe.bike.domain.model.entity.BikeEntity;
import com.safe.bike.domain.model.entity.BikeTypeEntity;
import com.safe.bike.domain.model.entity.BrandEntity;
import com.safe.bike.domain.model.entity.FrameTypeEntity;

import com.safe.bike.domain.port.in.BikeServicePort;
import com.safe.bike.domain.port.in.BikeTypeServicePort;
import com.safe.bike.domain.port.in.BrandServicePort;
import com.safe.bike.domain.port.in.FrameTypeServicePort;

import com.safe.user.adapter.out.persistence.entity.UserEntity;
import com.safe.user.infrastructure.mapper.UserMapper;
import com.safe.user.model.User;
import com.safe.user.infrastructure.port.UserServicePort;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.data.binder.Binder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Route("bike-form")
public class BikeFormView extends VerticalLayout {
    private static final Logger logger = LoggerFactory.getLogger(BikeFormView.class);

    private final BikeServicePort bikeService;
    private final BrandServicePort brandService;
    private final BikeTypeServicePort bikeTypeService;
    private final FrameTypeServicePort frameTypeService;
    private final UserServicePort userService;

    private final UserMapper userMapper;

    // Campos del formulario
    private ComboBox<User> userComboBox = new ComboBox<>("Usuario");
    private ComboBox<BrandEntity> brandComboBox = new ComboBox<>("Marca");
    private TextField serialNumberField = new TextField("Número de Serie");
    private ComboBox<BikeTypeEntity> bikeTypeComboBox = new ComboBox<>("Tipo de Bicicleta");
    private ComboBox<FrameTypeEntity> frameTypeComboBox = new ComboBox<>("Tipo de Cuadro");
    private DatePicker purchaseDateField = new DatePicker("Fecha de Compra");
    private NumberField purchaseValueField = new NumberField("Valor de Compra");

    // Botón de guardado
    private Button saveButton = new Button("Guardar Bicicleta");

    // Binder para enlazar los campos a la entidad
    private Binder<BikeEntity> binder = new Binder<>(BikeEntity.class);

    @Autowired
    public BikeFormView(
            BikeServicePort bikeService,
            BrandServicePort brandService,
            BikeTypeServicePort bikeTypeService,
            FrameTypeServicePort frameTypeService,
            UserServicePort userService, UserMapper userMapper) {
        this.userMapper = userMapper;

        logger.info("Inicializando BikeFormView");

        this.bikeService = bikeService;
        this.brandService = brandService;
        this.bikeTypeService = bikeTypeService;
        this.frameTypeService = frameTypeService;
        this.userService = userService;

        // Configurar los ComboBox con los datos de la base de datos
        configureComboBoxes();

        // Configurar el Binder
        binder.forField(userComboBox).bind(BikeEntity::getUserInMemory, BikeEntity::setUserInMemory);
        binder.forField(brandComboBox).bind(BikeEntity::getBrand, BikeEntity::setBrand);
        binder.forField(serialNumberField).bind(BikeEntity::getSerialNumber, BikeEntity::setSerialNumber);
        binder.forField(bikeTypeComboBox).bind(BikeEntity::getBikeType, BikeEntity::setBikeType);
        binder.forField(frameTypeComboBox).bind(BikeEntity::getFrameType, BikeEntity::setFrameType);
        binder.forField(purchaseDateField).bind(BikeEntity::getPurchaseDate, BikeEntity::setPurchaseDate);
        binder.forField(purchaseValueField).bind(BikeEntity::getPurchaseValue, BikeEntity::setPurchaseValue);

        // Configurar el botón de guardar
        saveButton.addClickListener(event -> saveBike());

        // Layout del formulario
        FormLayout formLayout = new FormLayout();
        formLayout.add(userComboBox, brandComboBox, serialNumberField, bikeTypeComboBox, frameTypeComboBox,
                purchaseDateField, purchaseValueField, saveButton);

        add(formLayout);
        setAlignItems(Alignment.CENTER);

        logger.info("BikeFormView inicializado correctamente");
    }

    private void configureComboBoxes() {
        logger.info("Configurando ComboBoxes del formulario");

        try {
            // Cargar usuarios
            logger.info("Cargando usuarios para ComboBox");
            List<User> users = userMapper.toDomainList(userService.getAllUsers());
            userComboBox.setItems(users);
            userComboBox.setItemLabelGenerator(user -> {
                // Ajusta según los campos de tu entidad User
                String name = user.getFirstName(); // o user.getUsername(), user.getEmail(), etc.
                if (name == null || name.trim().isEmpty()) {
                    return "Usuario ID: " + user.getId();
                }
                return name;
            });
            logger.info("ComboBox de usuarios configurado con {} elementos", users.size());

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

            // Cargar los tipos de bicicleta
            logger.info("Cargando tipos de bicicleta para ComboBox");
            List<BikeTypeEntity> bikeTypes = bikeTypeService.getAllBikeTypes();
            bikeTypeComboBox.setItems(bikeTypes);
            bikeTypeComboBox.setItemLabelGenerator(bikeType -> {
                String type = bikeType.getType();
                if (type == null || type.trim().isEmpty()) {
                    logger.warn("Tipo de bicicleta encontrado con type null o vacío: {}", bikeType);
                    return "Sin tipo";
                }
                return type;
            });
            logger.info("ComboBox de tipos de bicicleta configurado con {} elementos", bikeTypes.size());

            // Cargar los tipos de cuadro
            logger.info("Cargando tipos de cuadro para ComboBox");
            List<FrameTypeEntity> frameTypes = frameTypeService.getAllFrameTypes();
            frameTypeComboBox.setItems(frameTypes);
            frameTypeComboBox.setItemLabelGenerator(frameType -> {
                String type = frameType.getType();
                if (type == null || type.trim().isEmpty()) {
                    logger.warn("Tipo de cuadro encontrado con type null o vacío: {}", frameType);
                    return "Sin tipo";
                }
                return type;
            });
            logger.info("ComboBox de tipos de cuadro configurado con {} elementos", frameTypes.size());

        } catch (Exception e) {
            logger.error("Error al configurar ComboBoxes", e);
            Notification.show("Error al cargar los datos del formulario: " + e.getMessage(),
                    5000, Notification.Position.MIDDLE);
        }
    }

    private void saveBike() {
        logger.info("Intentando guardar bicicleta");

        BikeEntity bike = new BikeEntity();
        if (binder.writeBeanIfValid(bike)) {
            try {
                logger.debug("Datos de la bicicleta a guardar: {}", bike);
                logger.info("Usuario asignado: ID={}", bike.getUser() != null ? bike.getUser().getId() : "null");

                bikeService.save(bike);
                logger.info("Bicicleta guardada exitosamente con ID: {}", bike.getBikeType());

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