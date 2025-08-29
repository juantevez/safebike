package com.safe.bike.infrastructure.web.component;


import com.safe.bike.infrastructure.web.security.CurrentUserManager;
import com.safe.user.application.service.AuthService;
import com.safe.user.domain.model.User;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.server.VaadinSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BikeFormHeader extends HorizontalLayout {

    private static final Logger logger = LoggerFactory.getLogger(BikeFormHeader.class);

    private final CurrentUserManager currentUserManager;
    private final AuthService authService;

    private Span userInfo = new Span();

    public BikeFormHeader(CurrentUserManager currentUserManager, AuthService authService) {
        this.currentUserManager = currentUserManager;
        this.authService = authService;
        configureLayout();
    }

    private void configureLayout() {
        setWidthFull();
        setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        setAlignItems(FlexComponent.Alignment.CENTER);
        getStyle().set("padding", "10px 20px")
                .set("background-color", "#f5f5f5")
                .set("border-bottom", "1px solid #ddd");

        // Título
        Div titleSection = new Div(new H3("Registro de Bicicleta"));
        titleSection.getStyle().set("margin", "0").set("color", "#2c3e50");

        // Información del usuario
        userInfo.getStyle().set("color", "#7f8c8d").set("font-size", "14px");
        Div leftSection = new Div(titleSection, userInfo);

        // Botón de logout
        Button logoutButton = new Button("Cerrar Sesión", new Icon(VaadinIcon.SIGN_OUT));
        logoutButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        logoutButton.getStyle().set("color", "#e74c3c");
        logoutButton.getElement().setAttribute("title", "Cerrar sesión");
        logoutButton.addClickListener(e -> performLogout());

        add(leftSection, logoutButton);
    }

    public void updateUserInfo(User user) {
        if (user != null) {
            userInfo.setText("Bienvenido: " + user.getFirstName() + " " + user.getLastName());
        } else {
            userInfo.setText("Usuario no autenticado");
        }
    }

    private void performLogout() {
        String token = (String) VaadinSession.getCurrent().getAttribute("authToken");
        if (token != null) {
            authService.logout(token);
        }
        currentUserManager.clearSession();
        Notification.show("✅ Sesión cerrada", 2000, Notification.Position.TOP_CENTER);
        UI.getCurrent().navigate("login");
    }
}
