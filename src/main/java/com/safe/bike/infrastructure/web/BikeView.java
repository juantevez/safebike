package com.safe.bike.infrastructure.web;

import com.safe.bike.domain.model.entity.BikeEntity;
import com.safe.bike.domain.model.entity.BikeTypeEntity;
import com.safe.bike.domain.model.entity.BrandEntity;
import com.safe.bike.domain.model.entity.FrameTypeEntity;
import com.safe.bike.domain.port.in.BikeServicePort;
import com.safe.bike.domain.port.in.BikeTypeServicePort;
import com.safe.bike.domain.port.in.BrandServicePort;
import com.safe.bike.domain.port.in.FrameTypeServicePort;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.data.binder.Binder;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Route("bike-form")
public class BikeFormView extends VerticalLayout {

    private final BikeServicePort bikeService;
    private final BrandServicePort brandService;
    private final BikeTypeServicePort bikeTypeService;
    private final FrameTypeServicePort frameTypeService;

    // Campos del formulario
    private ComboBox<BrandEntity> brandComboBox = new ComboBox<>("Marca");
    private TextField serialNumberField = new TextField("Número de Serie");
    private ComboBox<BikeTypeEntity> bikeTypeComboBox = new ComboBox<>("Tipo de Bicicleta");
    private ComboBox<FrameTypeEntity> frameTypeComboBox = new ComboBox<>("Tipo de Cuadro");
    private DatePicker purchaseDateField = new DatePicker("Fecha de Compra");
    private NumberField purchaseValueField = new NumberField("Valor de Compra");
    private DateTimePicker createdAtField = new DateTimePicker("Creado En");

    // Botón de guardado
    private Button saveButton = new Button("Guardar Bicicleta");

    // Binder para enlazar los campos a la entidad
    private Binder<BikeEntity> binder = new Binder<>(BikeEntity.class);

    @Autowired
    public BikeFormView(
            BikeServicePort bikeService,
            BrandServicePort brandService,
            BikeTypeServicePort bikeTypeService,
            FrameTypeServicePort frameTypeService) {

        this.bikeService = bikeService;
        this.brandService = brandService;
        this.bikeTypeService = bikeTypeService;
        this.frameTypeService = frameTypeService;

        // Configurar los ComboBox con los datos de la base de datos
        configureComboBoxes();

        // Configurar el Binder
        binder.forField(brandComboBox).bind(BikeEntity::getBrand, BikeEntity::setBrand);
        binder.forField(serialNumberField).bind(BikeEntity::getSerialNumber, BikeEntity::setSerialNumber);
        binder.forField(bikeTypeComboBox).bind(BikeEntity::getBikeType, BikeEntity::setBikeType);
        binder.forField(frameTypeComboBox).bind(BikeEntity::getFrameType, BikeEntity::setFrameType);
        binder.forField(purchaseDateField).bind(BikeEntity::getPurchaseDate, BikeEntity::setPurchaseDate);
        binder.forField(purchaseValueField).bind(BikeEntity::getPurchaseValue, BikeEntity::setPurchaseValue);
        binder.forField(createdAtField).bind(BikeEntity::getCreatedAt, BikeEntity::setCreatedAt);

        // Configurar el botón de guardar
        saveButton.addClickListener(event -> saveBike());

        // Layout del formulario
        FormLayout formLayout = new FormLayout();
        formLayout.add(brandComboBox, serialNumberField, bikeTypeComboBox, frameTypeComboBox,
                purchaseDateField, purchaseValueField, createdAtField, saveButton);

        add(formLayout);
        setAlignItems(Alignment.CENTER);
    }

    private void configureComboBoxes() {
        // Cargar las marcas desde el servicio
        List<BrandEntity> brands = brandService.getAllBrands();
        brandComboBox.setItems(brands);
        brandComboBox.setItemLabelGenerator(BrandEntity::getName); // Muestra el nombre de la marca

        // Cargar los tipos de bicicleta
        List<BikeTypeEntity> bikeTypes = bikeTypeService.getAllBikeTypes();
        bikeTypeComboBox.setItems(bikeTypes);
        bikeTypeComboBox.setItemLabelGenerator(BikeTypeEntity::getType);

        // Cargar los tipos de cuadro
        List<FrameTypeEntity> frameTypes = frameTypeService.getAllFrameTypes();
        frameTypeComboBox.setItems(frameTypes);
        frameTypeComboBox.setItemLabelGenerator(FrameTypeEntity::getType);
    }

    private void saveBike() {
        BikeEntity bike = new BikeEntity();
        if (binder.writeBeanIfValid(bike)) {
            try {
                // Asignar el usuario (ejemplo, en un contexto real lo obtendrías de la sesión)
                // bike.setUser(...);

                bikeService.save(bike);
                Notification.show("Bicicleta guardada exitosamente!", 3000, Notification.Position.MIDDLE);
                binder.readBean(null); // Limpiar el formulario
            } catch (Exception e) {
                Notification.show("Error al guardar: " + e.getMessage(), 5000, Notification.Position.MIDDLE);
            }
        } else {
            Notification.show("Hay errores en el formulario. Por favor, corrígelos.", 3000, Notification.Position.MIDDLE);
        }
    }
}