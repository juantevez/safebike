package com.safe.user.infrastructure.web;

import com.safe.user.application.service.AuthService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.Autocapitalize;
import com.vaadin.flow.component.textfield.Autocomplete;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

@Route(value = "login", layout = MainLayout.class)
public class LoginView extends VerticalLayout {

    private final AuthService authService;

    private TextField email = new TextField("Email");
    private TextField password = new TextField("Contraseña");
    private Button loginButton = new Button("Iniciar Sesión");

    public LoginView(AuthService authService) {
        this.authService = authService;
        password.setPlaceholder("Contraseña");
        password.setAutocomplete(Autocomplete.NEW_PASSWORD);
        password.setAutocorrect(false);
        password.setAutocapitalize(Autocapitalize.NONE);

        loginButton.addClickListener(e -> login());

        add(
                new H2("🔐 Iniciar Sesión"),
                email,
                password,
                loginButton
        );
    }

    private void login() {
        try {
            String token = authService.login(email.getValue(), password.getValue());
            if (token != null) {
                getUI().ifPresent(ui -> {
                    ui.getSession().setAttribute("authToken", token);
                    ui.getSession().setAttribute("userEmail", email.getValue());
                    ui.navigate("bike-form"); // ← Ir a la vista de BIKE FORM
                });
            }
        } catch (Exception ex) {
            Notification.show("❌ " + ex.getMessage());
        }
    }
}