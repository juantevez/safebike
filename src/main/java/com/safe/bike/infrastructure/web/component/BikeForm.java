package com.safe.bike.infrastructure.web.component;

import com.safe.bike.domain.model.dto.BikeModelDto;
import com.safe.bike.domain.model.entity.*;
import com.safe.bike.domain.port.in.*;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BikeForm extends FormLayout {

    private static final Logger logger = LoggerFactory.getLogger(BikeForm.class);

    private final BrandServicePort brandService;
    private final BikeTypeServicePort bikeTypeService;
    private final BikeModelServicePort bikeModelServicePort;
    private final SizeServicePort sizeServicePort;
    private final MonedaServicePort monedaServicePort;

    private ComboBox<BrandEntity> brandComboBox = new ComboBox<>("Marca");
    private ComboBox<BikeTypeEntity> bikeTypeComboBox = new ComboBox<>("Tipo de Bicicleta");
    private ComboBox<BikeModelDto> bikeModelComboBox = new ComboBox<>("Modelo");
    private ComboBox<SizeEntity> sizeBikeComboBox = new ComboBox<>("Talle");
    private TextField serialNumberField = new TextField("Número de Serie");
    private DatePicker purchaseDateField = new DatePicker("Fecha de Compra");
    private ComboBox<MonedaEntity> monedaComboBox = new ComboBox<>("Moneda");
    private NumberField purchaseValueField = new NumberField("Valor de Compra");
    private Button saveButton = new Button("Guardar Bicicleta");

    private Binder<BikeEntity> binder = new Binder<>(BikeEntity.class);

    private final Map<BikeModelDto, BikeModelEntity> dtoToEntityMap = new HashMap<>();

    public BikeForm(
            BrandServicePort brandService,
            BikeTypeServicePort bikeTypeService,
            BikeModelServicePort bikeModelServicePort,
            SizeServicePort sizeServicePort,
            MonedaServicePort monedaServicePort) {
        this.brandService = brandService;
        this.bikeTypeService = bikeTypeService;
        this.bikeModelServicePort = bikeModelServicePort;
        this.sizeServicePort = sizeServicePort;
        this.monedaServicePort = monedaServicePort;

        configureComponents();
        configureBinder();
        add(
                brandComboBox,
                bikeTypeComboBox,
                bikeModelComboBox,
                serialNumberField,
                sizeBikeComboBox,
                purchaseDateField,
                monedaComboBox,
                purchaseValueField,
                saveButton
        );
    }

    private final Map<BikeModelEntity, BikeModelDto> entityToDtoMap = new HashMap<>();
    private void configureComponents() {
        // Marca
        brandComboBox.setItemLabelGenerator(brand -> brand.getName());
        brandComboBox.setPlaceholder("Seleccione una marca");

        // Tipo de bicicleta
        bikeTypeComboBox.setItemLabelGenerator(type -> type.getName());
        bikeTypeComboBox.setPlaceholder("Seleccione un tipo");

        // Modelo
        bikeModelComboBox.setItemLabelGenerator(BikeModelDto::modelName);
        bikeModelComboBox.setPlaceholder("Seleccione marca y tipo");
        bikeModelComboBox.setEnabled(false);

        // Talle → siempre habilitado y con todos los tamaños
        sizeBikeComboBox.setItemLabelGenerator(SizeEntity::getSigla);
        sizeBikeComboBox.setPlaceholder("Seleccione un tamaño");
        sizeBikeComboBox.setEnabled(true); // Siempre habilitado

        // Moneda
        monedaComboBox.setItemLabelGenerator(moneda -> moneda.getCodigoMoneda());
        monedaComboBox.setWidth("75px");
        monedaComboBox.getStyle().set("font-family", "monospace");

        // Cargar datos iniciales
        loadInitialData();

        // Configurar filtros dinámicos (solo marca → tipo → modelo)
        configureDynamicFiltering();
    }

    private void loadInitialData() {
        brandComboBox.setItems(brandService.getAllBrands());
        bikeTypeComboBox.setItems(bikeTypeService.getAllBikeTypes());
        sizeBikeComboBox.setItems(sizeServicePort.findAllSizes()); // Todos los tamaños
        monedaComboBox.setItems(monedaServicePort.findAllMonedas());

        // Cargar modelos con detalles para el mapeo DTO → Entity
        List<BikeModelEntity> models = bikeModelServicePort.findAllWithDetails();
        for (BikeModelEntity entity : models) {
            BikeModelDto dto = new BikeModelDto(
                    entity.getIdBikeModel(),
                    entity.getModelName(),
                    entity.getBrand().getBrandId(),
                    entity.getBrand().getName(),
                    entity.getBikeType().getBikeTypeId(),
                    entity.getBikeType().getName()
            );
            dtoToEntityMap.put(dto, entity);
            entityToDtoMap.put(entity, dto); // ✅ Nuevo
        }
    }

    private void configureDynamicFiltering() {
        Runnable updateModels = () -> {
            BrandEntity brand = brandComboBox.getValue();
            BikeTypeEntity type = bikeTypeComboBox.getValue();

            bikeModelComboBox.clear();

            if (brand != null && type != null) {
                List<BikeModelDto> filtered = bikeModelServicePort.getModelsByBrandAndType(
                        brand.getBrandId().longValue(),
                        type.getBikeTypeId().longValue()
                );
                setModels(filtered, "No hay modelos para esta combinación");
            } else if (brand != null) {
                List<BikeModelDto> filtered = bikeModelServicePort.getModelsByBrand(brand.getBrandId().longValue());
                setModels(filtered, "No hay modelos para esta marca");
            } else if (type != null) {
                List<BikeModelDto> filtered = bikeModelServicePort.getModelsByType(type.getBikeTypeId().longValue());
                setModels(filtered, "No hay modelos para este tipo");
            } else {
                bikeModelComboBox.setEnabled(false);
                bikeModelComboBox.setPlaceholder("Seleccione marca y tipo");
            }
        };

        brandComboBox.addValueChangeListener(e -> updateModels.run());
        bikeTypeComboBox.addValueChangeListener(e -> updateModels.run());
    }

    private void setModels(List<BikeModelDto> models, String placeholder) {
        if (models != null && !models.isEmpty()) {
            bikeModelComboBox.setItems(models);
            bikeModelComboBox.setEnabled(true);
            bikeModelComboBox.setPlaceholder("Seleccione un modelo");
        } else {
            bikeModelComboBox.clear();
            bikeModelComboBox.setEnabled(false);
            bikeModelComboBox.setPlaceholder(placeholder);
        }
    }

    private void configureBinder() {
        binder.forField(brandComboBox)
                .asRequired("Requerido")
                .bind(BikeEntity::getBrand, BikeEntity::setBrand);

        binder.forField(bikeTypeComboBox)
                .asRequired("Requerido")
                .bind(BikeEntity::getBikeType, BikeEntity::setBikeType);

        binder.forField(bikeModelComboBox)
                .withConverter(
                        // Converter: de DTO → Entity
                        dto -> dto != null ? dtoToEntityMap.get(dto) : null,
                        // Reverse converter: de Entity → DTO
                        entity -> entity != null ? entityToDtoMap.get(entity) : null
                )
                .asRequired("Requerido")
                .bind(
                        // Getter: cómo obtener el valor del modelo (BikeEntity)
                        BikeEntity::getBikeModel,
                        // Setter: cómo asignarlo
                        BikeEntity::setBikeModel
                );

        binder.forField(sizeBikeComboBox)
                .asRequired("Requerido")
                .bind(BikeEntity::getSizeBike, BikeEntity::setSizeBike);

        binder.forField(serialNumberField)
                .asRequired("Requerido")
                .bind(BikeEntity::getSerialNumber, BikeEntity::setSerialNumber);

        binder.forField(purchaseDateField)
                .withValidator(d -> d == null || !d.isAfter(LocalDate.now()), "Fecha no puede ser futura")
                .bind(BikeEntity::getPurchaseDate, BikeEntity::setPurchaseDate);

        binder.forField(monedaComboBox)
                .bind(BikeEntity::getMoneda, BikeEntity::setMoneda);

        binder.forField(purchaseValueField)
                .bind(BikeEntity::getPurchaseValue, BikeEntity::setPurchaseValue);
    }

    public boolean isValid() {
        return binder.isValid();
    }

    public boolean writeBeanIfValid(BikeEntity bike) {
        return binder.writeBeanIfValid(bike);
    }

    public void readBean(BikeEntity bike) {
        binder.readBean(bike);
    }

    public void addSaveListener(ComponentEventListener<ClickEvent<Button>> listener) {
        saveButton.addClickListener(listener);
    }

    public BikeModelEntity getSelectedModelEntity() {
        BikeModelDto dto = bikeModelComboBox.getValue();
        return dto != null ? dtoToEntityMap.get(dto) : null;
    }

    // Getters (opcionales, si los necesitas desde fuera)
    public ComboBox<BrandEntity> getBrandComboBox() { return brandComboBox; }
    public ComboBox<BikeTypeEntity> getBikeTypeComboBox() { return bikeTypeComboBox; }
    public ComboBox<BikeModelDto> getBikeModelComboBox() { return bikeModelComboBox; }
    public ComboBox<SizeEntity> getSizeBikeComboBox() { return sizeBikeComboBox; }
    public TextField getSerialNumberField() { return serialNumberField; }
    public DatePicker getPurchaseDateField() { return purchaseDateField; }
    public ComboBox<MonedaEntity> getMonedaComboBox() { return monedaComboBox; }
    public NumberField getPurchaseValueField() { return purchaseValueField; }
}