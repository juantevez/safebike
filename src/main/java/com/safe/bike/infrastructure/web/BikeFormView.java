package com.safe.bike.infrastructure.web;

import com.safe.bike.domain.model.entity.BikeEntity;
import com.safe.bike.domain.model.entity.BikeModelEntity;
import com.safe.bike.domain.port.in.*;
import com.safe.bike.infrastructure.web.component.BikeForm;
import com.safe.bike.infrastructure.web.component.BikeFormHeader;
import com.safe.bike.infrastructure.web.security.CurrentUserManager;
import com.safe.bike.infrastructure.web.service.BikeFormService;
import com.safe.user.infrastructure.adapters.output.persistence.entities.UserEntity;
import com.safe.user.application.service.AuthService;
import com.safe.user.domain.model.User;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@Route("bike-form")
public class BikeFormView extends VerticalLayout {

    private static final Logger logger = LoggerFactory.getLogger(BikeFormView.class);

    private final BikeFormService bikeFormService;
    private final CurrentUserManager currentUserManager;
    private final BikeForm bikeForm;
    private final BikeFormHeader header;

    private User currentUser;

    @Autowired
    public BikeFormView(
            BikeFormService bikeFormService,
            CurrentUserManager currentUserManager,
            BrandServicePort brandService,
            BikeTypeServicePort bikeTypeService,
            BikeModelServicePort bikeModelServicePort,
            SizeServicePort sizeServicePort,
            MonedaServicePort monedaServicePort,
            AuthService authService) {

        this.bikeFormService = bikeFormService;
        this.currentUserManager = currentUserManager;

        this.bikeForm = new BikeForm(brandService, bikeTypeService, bikeModelServicePort, sizeServicePort, monedaServicePort);
        this.header = new BikeFormHeader(currentUserManager, authService);

        add(header, bikeForm);
        setAlignItems(Alignment.CENTER);

        initCurrentUser();
        header.updateUserInfo(currentUser);
    }

    private void initCurrentUser() {
        currentUser = currentUserManager.getCurrentUser()
                .orElseThrow(() -> {
                    Notification.show("Acceso denegado. Inicie sesión.", 5000, Notification.Position.MIDDLE);
                    UI.getCurrent().navigate("login");
                    return new RuntimeException("Usuario no autenticado");
                });
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        bikeForm.addSaveListener(e -> saveBike());
    }

    private void saveBike() {
        BikeEntity bike = new BikeEntity();
        bike.setUser(convertUserToUserEntity(currentUser));

        BikeModelEntity modelEntity = bikeForm.getSelectedModelEntity();
        if (modelEntity != null) {
            bike.setBikeModel(modelEntity);
        }

        if (bikeForm.writeBeanIfValid(bike)) {
            try {
                bikeFormService.saveBike(bike);
                Notification.show("✅ Guardado", 3000, Notification.Position.MIDDLE);
                bikeForm.readBean(new BikeEntity());
                bikeForm.getBrandComboBox().clear();
                bikeForm.getBikeTypeComboBox().clear();
                bikeForm.getSizeBikeComboBox().clear();
                bikeForm.getBikeModelComboBox().clear();
            } catch (Exception ex) {
                logger.error("Error guardando bicicleta", ex);
                Notification.show("❌ Error: " + ex.getMessage(), 5000, Notification.Position.MIDDLE);
            }
        } else {
            Notification.show("Complete los campos obligatorios", 3000, Notification.Position.MIDDLE);
        }
    }

    private UserEntity convertUserToUserEntity(User user) {
        UserEntity entity = new UserEntity();
        entity.setId(user.getId());
        entity.setEmail(user.getEmail());
        entity.setFirstName(user.getFirstName());
        entity.setLastName(user.getLastName());
        // ... otros campos
        return entity;
    }
}